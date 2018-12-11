package de.objectiveit.kempdnsscaler.loadbalancer;

import de.objectiveit.kempdnsscaler.model.VirtualService;

import java.util.List;

/**
 * LoadBalancer interface to implement.
 */
public interface LoadBalancer {

    /**
     * Returns current list of Real Server IP addresses for the specified Virtual Service {@code vs}.
     *
     * @param vs Virtual Service
     * @return list of Real Server IP addresses
     */
    List<String> getRSList(VirtualService vs);

    /**
     * Adds Real Server IP to the specified Virtual Service {@code vs}.
     *
     * @param vs     Virtual Service
     * @param rsIP   Real Service IP to add
     * @param rsPort Real Service port number
     * @return {@code true} if successfully added, {@code false} otherwise
     */
    boolean addRS(VirtualService vs, String rsIP, int rsPort);

    /**
     * Deletes Real Server IP from the specified Virtual Service {@code vs}.
     *
     * @param vs     Virtual Service
     * @param rsIP   Real Service IP to delete
     * @param rsPort Real Service port number
     * @return {@code true} if successfully deleted, {@code false} otherwise
     */
    boolean delRS(VirtualService vs, String rsIP, int rsPort);

}
