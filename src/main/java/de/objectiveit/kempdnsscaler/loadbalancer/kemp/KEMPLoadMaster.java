package de.objectiveit.kempdnsscaler.loadbalancer.kemp;

import com.jcabi.xml.XMLDocument;
import de.objectiveit.kempdnsscaler.ApplicationException;
import de.objectiveit.kempdnsscaler.loadbalancer.LoadBalancer;
import de.objectiveit.kempdnsscaler.model.Credentials;
import de.objectiveit.kempdnsscaler.model.VirtualService;
import de.objectiveit.kempdnsscaler.util.HttpUtil;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static de.objectiveit.kempdnsscaler.util.HttpUtil.isValidURL;

/**
 * {@link de.objectiveit.kempdnsscaler.loadbalancer.LoadBalancer} interface implementation for KEMP LoadMaster.
 */
public class KEMPLoadMaster implements LoadBalancer {

    private String baseUrl;
    private Credentials credentials;

    public KEMPLoadMaster(String url, Credentials credentials) {
        if (!isValidURL(url)) {
            String message = MessageFormat.format("LoadMaster URL '{0}' is not valid, valid examples: 'https://somehost.com:443' or 'http://123.123.123.123'", url);
            throw new ApplicationException(message);
        }
        if (credentials.getLogin() == null || credentials.getPassword() == null) {
            throw new ApplicationException("LoadMaster credentials are required!");
        }
        this.baseUrl = url;
        this.credentials = credentials;
    }

    @Override
    public List<String> getRSList(VirtualService vs) {
        try {
            String showVS = this.baseUrl + "/access/showvs";
            Map<String, Object> queryParams = new HashMap<String, Object>() {{
                put("vs", vs.getIp());
                put("port", vs.getPort());
                put("prot", vs.getProtocol());
            }};
            String result = HttpUtil.doGet(showVS, queryParams, this.credentials);

            XMLDocument xml = new XMLDocument(result);
            return xml.xpath("/Response/Success/Data/Rs/Addr/text()");
        } catch (IOException e) {
            // no reason to continue
            throw new ApplicationException("Fatal error occurred", e);
        }
    }

    @Override
    public boolean addRS(VirtualService vs, String rsIP, int rsPort) {
        try {
            String addRS = this.baseUrl + "/access/addrs";
            Map<String, Object> queryParams = new HashMap<String, Object>() {{
                put("vs", vs.getIp());
                put("port", vs.getPort());
                put("prot", vs.getProtocol());
                put("rs", rsIP);
                put("rsport", rsPort);
                put("non_local", 1);
            }};
            // if 200 (OK) - doGet() will return non-null value, otherwise exception will be thrown
            HttpUtil.doGet(addRS, queryParams, this.credentials);
            return true;
        } catch (IOException e) {
            // no need in the stack trace at least for KEMP
            return false;
        }
    }

    @Override
    public boolean delRS(VirtualService vs, String rsIP, int rsPort) {
        try {
            String addRS = this.baseUrl + "/access/delrs";
            Map<String, Object> queryParams = new HashMap<String, Object>() {{
                put("vs", vs.getIp());
                put("port", vs.getPort());
                put("prot", vs.getProtocol());
                put("rs", rsIP);
                put("rsport", rsPort);
            }};
            // if 200 (OK) - doGet() will return non-null value, otherwise exception will be thrown
            HttpUtil.doGet(addRS, queryParams, this.credentials);
            return true;
        } catch (IOException e) {
            // no need in the stack trace at least for KEMP
            return false;
        }
    }

}
