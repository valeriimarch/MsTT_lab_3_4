import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NavigatorAgent extends Agent {

    @Override
    protected void setup() {
        System.out.println("Hello! The navigator agent " + getAID().getName() + " is ready.");

        DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName(getAID());
        ServiceDescription sd = new ServiceDescription();
        sd.setType(WumpusWorldAgent.Constants.NAVIGATOR_AGENT_TYPE);
        sd.setName(WumpusWorldAgent.Constants.NAVIGATOR_SERVICE_DESCRIPTION);
        dfd.addServices(sd);
        try {
            DFService.register(this, dfd);
        } catch (FIPAException fe) {
            fe.printStackTrace();
        }

        addBehaviour(new LocationRequestsServer());
    }

    @Override
    protected void takeDown() {
        try {
            DFService.deregister(this);
        } catch (FIPAException fe) {
            fe.printStackTrace();
        }
        System.out.println("The navigator agent " + getAID().getName() + " terminating.");
    }

    private static class LocationRequestsServer extends CyclicBehaviour {

        int time = 0;

        @Override
        public void action() {
            MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.INFORM);
            ACLMessage msg = myAgent.receive(mt);
            if (msg != null) {
                if (parseSpeleologistMessageRequest(msg.getContent())){
                    ACLMessage reply = msg.createReply();
                    reply.setPerformative(ACLMessage.REQUEST);
                    reply.setContent(WumpusWorldAgent.Constants.INFORMATION_PROPOSAL_NAVIGATOR);
                    System.out.println("NavigatorAgent: " + WumpusWorldAgent.Constants.INFORMATION_PROPOSAL_NAVIGATOR);
                    myAgent.send(reply);
                } else if (parseSpeleologistMessageProposal(msg.getContent()))
                {
                    ACLMessage reply = msg.createReply();
                    reply.setPerformative(ACLMessage.PROPOSE);
                    String advice = getAdvice(msg.getContent());
                    reply.setContent(advice);
                    System.out.println("NavigatorAgent: " + advice);
                    myAgent.send(reply);

                } else
                    System.out.println("NavigatorAgent: Wrong message!");
            } else {
                block();
            }
        }

        private boolean parseSpeleologistMessageRequest(String instruction) {
            String regex = "\\bHelp\\b";
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(instruction);
            if (matcher.find()) {
                String res = matcher.group();
                return res.length() > 0;
            }
            return false;
        }

        private boolean parseSpeleologistMessageProposal(String instruction) {
            String regex = "\\bPosition\\b";
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(instruction);
            if (matcher.find()) {
                String res = matcher.group();
                return res.length() > 0;
            }
            return false;
        }

        private String getAdvice(String content){
            boolean stench = false;
            boolean breeze = false;
            boolean glitter = false;
            boolean scream = false;
            String advisedAction = "";

            for (Map.Entry<Integer, String> entry : STATES.entrySet()) {
                String value = entry.getValue();
                Pattern pattern = Pattern.compile("\\b" + value + "\\b", Pattern.CASE_INSENSITIVE);
                Matcher matcher = pattern.matcher(content);
                if (matcher.find()) {
                    switch (value){
                        case "Stench": stench = true;
                        case "Breeze": breeze = true;
                        case "Glitter": glitter = true;
                        case "Scream": scream = true;
                    }
                }
            }

            switch (time) {
                case 0 -> {
                    advisedAction = WumpusWorldAgent.Constants.MESSAGE_RIGHT;
                    time++;
                }
                case 1, 3, 4, 8, 10, 11 -> {
                    advisedAction = WumpusWorldAgent.Constants.MESSAGE_FORWARD;
                    time++;
                }
                case 2, 6, 9 -> {
                    advisedAction = WumpusWorldAgent.Constants.MESSAGE_LEFT;
                    time++;
                }
                case 5 -> {
                    advisedAction = WumpusWorldAgent.Constants.MESSAGE_GRAB;
                    time++;
                }
                case 7 -> {
                    advisedAction = WumpusWorldAgent.Constants.MESSAGE_SHOOT;
                    time++;
                }
                case 12 -> {
                    advisedAction = WumpusWorldAgent.Constants.MESSAGE_CLIMB;
                    time++;
                }
            }

            int rand = 1 + (int) (Math.random() * 3);
            return switch (rand) {
                case 1 -> WumpusWorldAgent.Constants.ACTION_PROPOSAL1 + advisedAction;
                case 2 -> WumpusWorldAgent.Constants.ACTION_PROPOSAL2 + advisedAction;
                case 3 -> WumpusWorldAgent.Constants.ACTION_PROPOSAL3 + advisedAction;
                default -> "";
            };
        }

        final Map<Integer, String> STATES = new LinkedHashMap<>() {{
            put(1, "Stench");
            put(2, "Breeze");
            put(3, "Glitter");
            put(4, "Scream");
        }};

    }
}