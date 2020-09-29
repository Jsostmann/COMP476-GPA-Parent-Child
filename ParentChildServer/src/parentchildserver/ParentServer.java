/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package parentchildserver;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author jamesostmann
 */
public class ParentServer {
    
    private ServerSocket server;
    private static final HashMap<String, Double> GPA_TABLE;
    private int numTeams;
    private DataOutputStream toClient;
    private DataInputStream fromClient;
    private ChildTask[] childTasks;
    private Thread[] childThreads;
    
    
    public static void main(final String[] args) {
        
        new ParentServer(8999);
    }
    
    static {
        GPA_TABLE = new HashMap<>();
        initTable();
    }

    public ParentServer(int port) {
        
        int cores = Runtime.getRuntime().availableProcessors();
        System.out.println("Starting server with " + cores + " cores....");
        
        int currentTeam = 0;

        try {
            server = new ServerSocket(port);
            
            Socket socket = server.accept();
            fromClient = new DataInputStream(new BufferedInputStream(socket.getInputStream()));

            toClient = new DataOutputStream(socket.getOutputStream());

            String parentClientMessage = fromClient.readUTF();
            
            numTeams = Integer.parseInt(parentClientMessage);
            
            childTasks = new ChildTask[numTeams];
            childThreads = new Thread[numTeams];
            
            System.out.println(parentClientMessage);
            
           
            
            while (currentTeam < numTeams) {
                
                socket = server.accept();
                InetAddress ip = socket.getInetAddress();
                
                System.out.println("Established connection with new Child client at " + ip.getHostAddress());
                
                childTasks[currentTeam] = new ChildTask(socket);
                childThreads[currentTeam] = new Thread(childTasks[currentTeam]);
                
                childThreads[currentTeam].start();
                
                currentTeam++;

            }
            
            joinChildServers();
            
            Arrays.sort(childTasks);
            String result = "";
            for(ChildTask t: childTasks) {
                result += t.toString(); 
            }
            
            toClient.writeUTF(result);
            
          
        } catch (IOException ex) {
            Logger.getLogger(ParentServer.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
    
    private void joinChildServers() {
    
        try {
        
            for(Thread child: childThreads) {
                    
                child.join();
            
            }
        
        } catch (InterruptedException ex) {
            Logger.getLogger(ParentServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    
    }
    
    static void initTable() {
        GPA_TABLE.put("A", 4.0);
        GPA_TABLE.put("A-", 3.7);
        GPA_TABLE.put("B+", 3.3);
        GPA_TABLE.put("B", 3.0);
        GPA_TABLE.put("B-", 2.7);
        GPA_TABLE.put("C+", 2.3);
        GPA_TABLE.put("C", 2.0);
        GPA_TABLE.put("C-", 1.7);
        GPA_TABLE.put("D+", 1.3);
        GPA_TABLE.put("D", 1.0);
        GPA_TABLE.put("F", 0.0);
    }
    
    private static String calculateGPA(String clientMessage) {

        String[] message = clientMessage.split(",");
        int numGrades = Character.getNumericValue(message[0].charAt(0));
        int i;
        int semesterCredits = 0;
        double semesterTotal = 0;

        for (i = 1; i < numGrades * 2; i += 2) {
            semesterCredits += Integer.parseInt(message[i + 1]);
            semesterTotal += (GPA_TABLE.get(message[i]) * Integer.parseInt(message[i + 1]));
        }

        double semesterGPA = semesterTotal / semesterCredits;

        double prevGPA = Double.parseDouble(message[i++]);
        int prevCreditHrs = Integer.parseInt(message[i]);
        double cumulativeGPA = ((prevGPA * prevCreditHrs) + semesterTotal) / (prevCreditHrs += semesterCredits);
       
        return semesterGPA + "," + cumulativeGPA;
    }
    
    private class ChildTask implements Runnable, Comparable<ChildTask> {
        
        private Socket socket;
        private DataOutputStream toClient;
        private DataInputStream fromClient;
        private double teamSemesterGPA;
        private double teamCumulativeGPA;
        private double sumSemesterGPA;
        private double sumCumulativeGPA;
        private String teamName;
        
        public ChildTask(Socket socket) {
            this.socket = socket;
            teamSemesterGPA = 0.0;
            teamCumulativeGPA = 0.0;
            sumSemesterGPA = 0.0;
            sumCumulativeGPA = 0.0;
        }
        
        public double getTeamSemesterGPA() {
            return teamSemesterGPA;
        }
        
        public double getTeamCumulativeGpa() {
            return teamCumulativeGPA;
        }
        
        @Override
        public int compareTo(ChildTask other) {
            return this.teamName.compareTo(other.teamName);
        }
        
        @Override
        public String toString() {
            DecimalFormat df = new DecimalFormat("#.##");
            return String.format("%-12s %-12s %-12s",teamName,df.format(teamSemesterGPA),df.format(teamCumulativeGPA)) + System.lineSeparator();
        }
        
        @Override
        public void run() {

            try {
                
                fromClient = new DataInputStream(new BufferedInputStream(socket.getInputStream()));

                toClient = new DataOutputStream(socket.getOutputStream());

                teamName = fromClient.readUTF();
                
                System.out.println("Team: " + teamName);
                
                int numPlayers = Integer.parseInt(fromClient.readUTF());
                
                System.out.println("Num Players: " + numPlayers);
                
                
                for(int i = 0; i < numPlayers; i++) {
                    
                    String playerGpa = fromClient.readUTF();
                    System.out.println("Player " + i + " " + teamName + " " + playerGpa);
                    String[] tempPlayerData = calculateGPA(playerGpa).split(",");
                    sumSemesterGPA += Double.parseDouble(tempPlayerData[0]);
                    sumCumulativeGPA += Double.parseDouble(tempPlayerData[1]);
                    
                }
                
                
                teamSemesterGPA = sumSemesterGPA/ numPlayers;
                teamCumulativeGPA = sumCumulativeGPA / numPlayers;
                
                
                socket.close();
                toClient.close();
                fromClient.close();
                            
            } catch (IOException ex) {
                Logger.getLogger(ChildTask.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
         
    }
    
}
