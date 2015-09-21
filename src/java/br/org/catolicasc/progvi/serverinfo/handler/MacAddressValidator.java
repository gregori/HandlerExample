/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.org.catolicasc.progvi.serverinfo.handler;

import java.io.IOException;
import java.util.Collections;
import java.util.Iterator;
import java.util.Set;
import javax.xml.namespace.QName;
import javax.xml.rpc.soap.SOAPFaultException;
import javax.xml.soap.Node;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPConstants;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPFault;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPMessage;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;

/**
 *
 * @author rodrigo
 */
public class MacAddressValidator implements SOAPHandler<SOAPMessageContext> {
    
    @Override
    public boolean handleMessage(SOAPMessageContext context) {
        System.out.println("Server: handleMessage().......");
        Boolean isRequest = (Boolean) context.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);
        // Para mensagem de resposta apenas, True para saída, falso para entrada
        if (!isRequest) {
            try {
                SOAPMessage soapMsg = context.getMessage();
                SOAPEnvelope soapEnv = soapMsg.getSOAPPart().getEnvelope();
                SOAPHeader soapHeader = soapEnv.getHeader();
                
                // se não tem header, adiciona um
                if (soapHeader == null) {
                    soapHeader = soapEnv.addHeader();
                    generateSOAPErrMessage(soapMsg, "No SOAP header.");
                }
                
                // Obtem o MAC Address do SOAP Header
                Iterator it = soapHeader.extractHeaderElements(SOAPConstants.URI_SOAP_ACTOR_NEXT);
                
                // Se não há bloco header encontrado para o ator, lanca exceção
                if (it == null || !it.hasNext())
                    generateSOAPErrMessage(soapMsg, "No SOAP header for next actor.");
                
                // Se não achou um mac address, lança exceção
                Node macNode = (Node) it.next();
                String macValue = (macNode == null) ? null : macNode.getValue();
                
                if (macValue == null)
                    generateSOAPErrMessage(soapMsg, "No mac address in header block.");
                
                if (!macValue.equals("54:26:96:DC:E0:4B"))
                    generateSOAPErrMessage(soapMsg, "Invalid MAC address, access denied.");
                
                // para debug
                soapMsg.writeTo(System.out);
            } catch (SOAPException | IOException e) {
                System.out.println(e);
            }
        }
        
        // Continua a "chain" do handler
        return true;
    }
    
    @Override
    public Set<QName> getHeaders() {
        System.out.println("Server : getHeaders()......");
        return Collections.EMPTY_SET;
    }
    
    @Override
    public boolean handleFault(SOAPMessageContext messageContext) {
        System.out.println("Server : handleFault()......");
        return true;
    }
    
    @Override
    public void close(MessageContext context) {
        System.out.println("Server : close()......");
    }
    
    private void generateSOAPErrMessage(SOAPMessage msg, String reason) {
        try {
            SOAPBody soapBody = msg.getSOAPPart().getEnvelope().getBody();
            SOAPFault soapFault = soapBody.addFault();
            soapFault.setFaultString(reason);
            throw new SOAPFaultException(soapFault.getFaultCodeAsQName(), 
                    reason, soapFault.getFaultActor(), soapFault.getDetail());
        } catch (SOAPException e) { }
    }
    
}
