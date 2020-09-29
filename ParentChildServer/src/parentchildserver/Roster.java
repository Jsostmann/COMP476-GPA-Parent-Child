/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package parentchildserver;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

/**
 *
 * @author jamesostmanns
 */
public class Roster {
    
    private int numTeams;
    private ArrayList<Team> roster;
    
    public Roster() {
        numTeams = 0;
        roster = new ArrayList<>();
    }
    
   
    public void generateRoster() {
        
        try {
            
            File file = new File("sportsdata.txt");
          
            Scanner in = new Scanner(file);
           
            this.numTeams = Integer.parseInt(in.nextLine());
            
            while (in.hasNextLine()) {
                String raw = in.nextLine();
                String[] currentTeam = raw.trim().split(",");
                
                Team tempTeam = new Team(currentTeam[0],Integer.parseInt(currentTeam[1]));
                tempTeam.generateTeam();
                
                roster.add(tempTeam);
                
            }
            
        } catch(FileNotFoundException e) {
            
        }
        
    }
    
       
    @Override 
    public String toString() {
        String result = "";
        
        result += numTeams + System.lineSeparator();
        
        
        for(Team team: roster) {
        
            result += team.toString();
            
        }
        
        return result;
    }
    
    private class Team {
        private ArrayList<Player> team;
        private int numPlayers;
        private String teamName;
        
        public Team() {
            this(null,0);
        }
        
        public Team(String teamName, int numPlayers) {
            this.numPlayers = numPlayers;
            this.teamName = teamName;
            team = new ArrayList<>();
        }
        
        public String getTeamName() {
            return teamName;
        }
        
        public void setTeamName(String teamName) {
            this.teamName = teamName;
        }
        
        public int getNumPlayers() {
            return numPlayers;
        }

        public void setNumPlayers(int numPlayers) {
            this.numPlayers = numPlayers;
        }

        public ArrayList<Player> getTeam() {
            return team;
        }
        
        public void generateTeam() {
            
            for(int i = 0; i < numPlayers; i++) {
                
                team.add(new Player(GpaGenerator.generateGpaData()));
            
            }
        }
        
        @Override 
        public String toString() {
            String result = "";
            
            result += teamName + " " + numPlayers + System.lineSeparator(); 
            
            for(Player player: team) {
                
                result += player.toString();
                
            }
            
            return result;
        }

    }
    
    private class Player {
        
        private String Gpa;
        
        public Player() {
            this(null);
        }
        
        public Player(String Gpa) {
            this.Gpa = Gpa;
        }
        
        public void setGpa(String Gpa) {
            this.Gpa = Gpa;
        }
        
        public String getGpa() {
            return Gpa;
        }
        
        @Override 
        public String toString() {
            return Gpa + System.lineSeparator();
        }
    }
   
    
}
