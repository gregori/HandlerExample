/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.org.catolicasc.progvi.serverinfo;

import java.net.InetAddress;
import java.net.UnknownHostException;
import javax.jws.HandlerChain;
import javax.jws.WebService;
import javax.jws.WebMethod;
import javax.jws.WebParam;

/**
 *
 * @author rodrigo
 */
@WebService(serviceName = "ServerInfo")
@HandlerChain(file="handler-chain.xml")
public class ServerInfo {

    /**
     * This is a sample web service operation
     * @return 
     * @throws java.net.UnknownHostException
     */
    @WebMethod(operationName = "getServerName")
    public String getServerName() throws UnknownHostException {
        return InetAddress.getLocalHost().getHostName();
    }
}
