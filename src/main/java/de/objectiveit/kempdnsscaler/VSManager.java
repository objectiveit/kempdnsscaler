package de.objectiveit.kempdnsscaler;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.ScheduledEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.objectiveit.kempdnsscaler.loadbalancer.LoadBalancer;
import de.objectiveit.kempdnsscaler.loadbalancer.kemp.KEMPLoadMaster;
import de.objectiveit.kempdnsscaler.model.VSRequest;
import de.objectiveit.kempdnsscaler.model.VirtualService;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

import static de.objectiveit.kempdnsscaler.util.CollectionUtil.subtract;

/**
 * Main AWS Lambda function class, which implements {@link RequestHandler} and contains main logic.
 */
public class VSManager implements RequestHandler<ScheduledEvent, String> {

    @Override
    public String handleRequest(ScheduledEvent event, Context context) {
        LambdaLogger logger = context.getLogger();
        logger.log("<<< Event ID: " + event.getId() + "\n");

        // Initialization:
        logger.log("<<< [Init] Converting scheduled event to VSRequest object...\n");
        ObjectMapper mapper = new ObjectMapper();
        VSRequest request = mapper.convertValue(event.getDetail(), VSRequest.class);

        logger.log("<<< [Init] Initializing LoadBalancer interface...\n\n");
        LoadBalancer loadBalancer = new KEMPLoadMaster(request.getLoadBalancerURL(), request.getCredentials());

        // Step 1: List of current RS:
        logger.log("<<< [Step 1] Fetch list of current Real Server IPs for the specified Virtual Service...\n");
        VirtualService vs = request.getVs();
        List<String> currentIPs = loadBalancer.getRSList(vs);
        logger.log("<<< [Step 1] Done, result = " + currentIPs + "\n\n");

        // Step 2: nslookup for desired RS:
        List<String> rsIPs = step2Nslookup(logger, request.getRsIPs());

        // Step 3: Calculate changes to do:
        logger.log("<<< [Step 3] Calculate changes need to be done (RS list to add and to remove)...\n");
        List<String> toAdd = subtract(rsIPs, currentIPs);
        List<String> toRemove = subtract(currentIPs, rsIPs);
        logger.log("<<< [Step 3] Done, list of RS to add = " + toAdd + ", to remove = " + toRemove + "\n\n");

        // Step 4: Add RSs:
        int rsPort = request.getRsPort();
        int added = step4AddRSs(logger, loadBalancer, vs, toAdd, rsPort);
        if (added == 0 && currentIPs.size() == toRemove.size()) {
            throw new ApplicationException("[Step 4] No RS added because of failure(s) and requested to remove all current RSs, this will brake load balancing!");
        }

        // Step 5: Remove RSs:
        step5RemoveRSs(logger, loadBalancer, vs, toRemove, rsPort);

        // Return list of the result RS IPs:
        List<String> resultIPs = loadBalancer.getRSList(vs);
        return "Finished, result RS IPs = " + resultIPs;
    }

    private List<String> step2Nslookup(LambdaLogger logger, List<String> desiredRSIPs) {
        logger.log("<<< [Step 2] Prepare list of desired IPs (do nslookup to replace FQDNs with IPs if any)...\n");
        List<String> rsList = desiredRSIPs == null ? new ArrayList<>() : desiredRSIPs;
        logger.log("<<< [Step 2] List to analyze:" + rsList + "\n");
        if (rsList.size() == 0) {
            throw new ApplicationException("[Step 2] List of desired RS is empty, this will brake load balancing!");
        }
        List<String> rsIPs = new ArrayList<>();
        for (String next : rsList) {
            try {
                List<String> nextIPs = new ArrayList<>();
                for (InetAddress address : InetAddress.getAllByName(next)) {
                    String ip = address.getHostAddress();
                    nextIPs.add(ip);
                }
                rsIPs.addAll(nextIPs);
                logger.log("<<< [Step 2] [Correct] nslookup results: " + nextIPs + "\n");
            } catch (Exception e) {
                logger.log("<<< [Step 2] [Failure] nslookup failure for: " + next + "\n");
            }
        }
        logger.log("<<< [Step 2] Done, result = " + rsIPs + "\n\n");
        if (rsIPs.size() == 0) {
            throw new ApplicationException("[Step 2] List of desired RS IPs (after nslookup) is empty, this will brake load balancing!");
        }
        return rsIPs;
    }

    private int step4AddRSs(LambdaLogger logger, LoadBalancer loadBalancer, VirtualService vs, List<String> toAdd, int rsPort) {
        int added = 0;
        if (toAdd.isEmpty()) {
            logger.log("<<< [Step 4] No RS to add, skip this step!\n\n");
        } else {
            logger.log("<<< [Step 4] Start adding required RSs...\n");
            for (String nextRS : toAdd) {
                boolean result = loadBalancer.addRS(vs, nextRS, rsPort);
                if (result) {
                    logger.log("<<< [Step 4] [Success] Added: " + nextRS + "\n");
                    added++;
                } else {
                    logger.log("<<< [Step 4] [Failure] " + nextRS + "\n");
                }
            }
            logger.log("<<< [Step 4] Done, successfully added RS amount: " + added + "\n\n");
        }
        return added;
    }

    private void step5RemoveRSs(LambdaLogger logger, LoadBalancer loadBalancer, VirtualService vs, List<String> toRemove, int rsPort) {
        if (toRemove.isEmpty()) {
            logger.log("<<< [Step 5] No RS to remove, skip this step!\n\n");
        } else {
            int removed = 0;
            logger.log("<<< [Step 5] Start removing RSs...\n");
            for (String nextRS : toRemove) {
                boolean result = loadBalancer.delRS(vs, nextRS, rsPort);
                if (result) {
                    logger.log("<<< [Step 5] [Success] Removed: " + nextRS + "\n");
                    removed++;
                } else {
                    logger.log("<<< [Step 5] [Failure] " + nextRS + "\n");
                }
            }
            logger.log("<<< [Step 5] Done, successfully removed RS amount: " + removed + "\n\n");
        }
    }

}
