/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package parentchildserver;


import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ParentClient {

/**
 *
 * @author jamesostmann
 */
    private DataInputStream fromServer = null;
    private DataOutputStream toServer = null;
    private Socket socket = null;
    private Scanner input;
    private ClientChildTask[] childTasks;
    private Thread[] childThreads;
    
    public static void main(final String[] args) {
        new ParentClient(8999);
    }
    
    public ParentClient(int port) {
        
        try {
            socket = new Socket(InetAddress.getLocalHost(),port);
            fromServer = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
            toServer = new DataOutputStream(socket.getOutputStream());
            
            File file = new File("sportsdata.txt");
            input = new Scanner(file);
            
            int currentTeam = 0;
            int numTeams = Integer.parseInt(input.nextLine());
            
            childTasks = new ClientChildTask[numTeams];
            childThreads = new Thread[numTeams];
            
            System.out.println("Sending num teams to server...");
            
            toServer.writeUTF(String.valueOf(numTeams)); 
            
            while(input.hasNextLine()) {
                Socket tempSocket = new Socket(InetAddress.getLocalHost(),port);
                String[] tempTeam = input.nextLine().trim().split(",");
                childTasks[currentTeam] = new ClientChildTask(tempSocket,tempTeam[0],Integer.parseInt(tempTeam[1]));
                childThreads[currentTeam] = new Thread(childTasks[currentTeam]);
                childThreads[currentTeam].start();
                
                currentTeam++;
            }
            
            joinChildClients();
            
            System.out.println(fromServer.readUTF()); 
           
            
        } catch (UnknownHostException ex) {
            Logger.getLogger(ParentClient.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ParentClient.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
        private void joinChildClients() {
    
        try {
        
            for(Thread child: childThreads) {
                    
                child.join();
            
            }
        
        } catch (InterruptedException ex) {
            Logger.getLogger(ParentClient.class.getName()).log(Level.SEVERE, null, ex);
        }
    
    }
    
    
    
    private class ClientChildTask implements Runnable {
        private Socket socket;
        private int numPlayers;
        private String teamName;
        private DataOutputStream toServer;
        
        public ClientChildTask(Socket socket, String teamName, int numPlayers) {
            this.socket = socket;
            this.teamName = teamName;
            this.numPlayers = numPlayers;
        }
        
        @Override
        public void run() {
            try {

                toServer = new DataOutputStream(socket.getOutputStream());
                 
                toServer.writeUTF(teamName);
                toServer.writeUTF(String.valueOf(numPlayers)); 
                
                for(int i = 0; i < numPlayers; i++) {
                    String playerGpa = GpaGenerator.generateGpaData();
                    toServer.writeUTF(playerGpa);
                }
                
                toServer.close();
                socket.close();
                            
            } catch (IOException ex) {
                Logger.getLogger(ClientChildTask.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    
    }
    
    
    
   
}
