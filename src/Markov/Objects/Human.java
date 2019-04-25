package Markov.Objects;

import java.util.ArrayList;

import static Markov.Objects.Replies.listOfPerson;

public class Human {
    String name;
    ArrayList<String> mentionedBy = new ArrayList<>();
    //  int repeat = 0;
    boolean needPunish = true;
    public int assignedPuzzle = 0;
    public int assignedPersonality = 0;
    public String personalityName ="";
    public int fond = 0;

    public Human(String name) {
        this.name = name;
    }

    public String getReply() {
        String reply = listOfPerson.get(assignedPersonality).lines.get(fond);
        fond++;
        if (fond >= listOfPerson.get(assignedPersonality).lines.size()) {
            fond = 0;
            String temp = personalityName;
            setPersonality();
            return temp+"의 대사가 모두 해금되었습니다. 다음에는 다른 성격이 무작위로 배정됩니다. 또 원하는 성격이 있으면 건의주지 않을래~?";
        }
        // reply = reply.replace("제독",name);
        return reply;
    }

    public void setPersonality() {
        assignedPersonality = (int) (Math.random() * listOfPerson.size());
        personalityName = listOfPerson.get(assignedPersonality).personality;
    }

    public String getList() {
        if (mentionedBy.size() == 0) return "비어있음";
        String mList = mentionedBy.get(0);
        for (int i = 1; i < mentionedBy.size(); i++) {
            mList = mList + "," + mentionedBy.get(i);
        }
        return mList;
    }


    public static class HumanList {
        public ArrayList<Human> humanList = new ArrayList<>();

        public HumanList() {
            this.humanList = new ArrayList<>();
        }

        public boolean hasPendingPunish(String name) {
            for (int i = 0; i < humanList.size(); i++) {
                if (humanList.get(i).name.equals(name)) return humanList.get(i).needPunish;
            }
            return false;
        }


        public void addByName(String victim) {
            Human temp = new Human(victim);
            boolean duplicate = false;
            for (int i = 0; i < humanList.size(); i++) {
                if (humanList.get(i).name.equals(victim)) {
                    humanList.get(i).needPunish = true;
                    duplicate = true;
                    break;
                }
            }
            if (!duplicate) humanList.add(temp);
        }

        public Human getHuman(String a) {
            for (int i = 0; i < humanList.size(); i++) {
                if (humanList.get(i).name.equals(a)) return humanList.get(i);
            }
            return null;
        }

        public void add(String name) {
            Human temp = new Human(name);
            temp.setPersonality();
            humanList.add(temp);
        }

        public boolean contains(String name) {
            for (int i = 0; i < humanList.size(); i++) {
                if (humanList.get(i).name.equals(name)) return true;
            }
            return false;
        }

    }
}
