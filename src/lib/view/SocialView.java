package lib.view;

import lib.controller.SocialController;
import lib.controller.UserController;
import lib.model.Group;
import lib.model.Interaction;
import lib.model.Meeting;
import lib.model.User;
import lib.utilities.Utils;

import java.util.*;

enum SocialAction {
    GENERATE_FEED,
    POST_COMMENT,
    EDIT_COMMENT,
    REPLY_TO_COMMENT,
    LIKE_COMMENT,
    SHARE_COMMENT,
    LIST_GROUPS,
    CREATE_GROUP,
    UPDATE_GROUP,
    JOIN_GROUP,
    LEAVE_GROUP,
    LIST_MEETINGS,
    CREATE_MEETING,
    UPDATE_MEETING,
    CHECK_UPCOMING_MEETINGS
}

/**
 * This static class handles the social media view. Users may post 'interactions' and join social
 * media groups.
 */
public class SocialView {
    private static boolean exiting = false;

    /**
     * Initial intake. Returning a '1' indicates the user wants to switch modes, and returning a '0' indicates
     * that the user wants to quit.
     */
    public static int basePrompt(Scanner sc) {
        if (UserController.getCurrentUser() == null) {
            return 0;
        } //Should never happen


        System.out.println("Welcome to the Social Network, " + UserController.getCurrentUser().getFirstName() + ".");
        ArrayList<SocialAction> actions = new ArrayList<SocialAction>();
        actions.add(SocialAction.GENERATE_FEED);
        actions.add(SocialAction.POST_COMMENT);
        actions.add(SocialAction.EDIT_COMMENT);
        actions.add(SocialAction.REPLY_TO_COMMENT);
        actions.add(SocialAction.LIKE_COMMENT);
        actions.add(SocialAction.SHARE_COMMENT);
        actions.add(SocialAction.LIST_GROUPS);
        actions.add(SocialAction.CREATE_GROUP);
        actions.add(SocialAction.UPDATE_GROUP);
        actions.add(SocialAction.JOIN_GROUP);
        actions.add(SocialAction.LEAVE_GROUP);
        actions.add(SocialAction.LIST_MEETINGS);
        actions.add(SocialAction.CREATE_MEETING);
        actions.add(SocialAction.UPDATE_MEETING);
        actions.add(SocialAction.CHECK_UPCOMING_MEETINGS);
        while (!exiting){
            System.out.println("Please select one of the actions available, or 'exit' to exit.");
            System.out.println("You'll need an id number to reply to comments or interact with groups.");
            System.out.println("Type 'library' to switch to the library view.");
            for (int i = 0; i < actions.size(); i++){
                System.out.println((i + 1) + ". " + printAction(actions.get(i)));
            }
            String input = sc.next();
            if (input.equals("library")){
                return 1;
            } else {
                if (!input.equals("exit")) {
                    SocialAction action = actions.get(Integer.parseInt(input) - 1); //Sanitize
                    dispatchAction(action, sc);
                } else {
                    exiting = true;
                }
            }
        }
        return 0;
    }

    private static String printAction(SocialAction action){
        switch (action){
            case GENERATE_FEED -> {return "Show your social media feed.";}
            case POST_COMMENT -> {return "Post a comment to a feed.";}
            case EDIT_COMMENT -> {return "Edit one of your comments.";}
            case REPLY_TO_COMMENT -> {return "Reply to a comment.";}
            case LIKE_COMMENT -> {return "Like a comment.";}
            case SHARE_COMMENT -> {return "Share a comment.";}
            case LIST_GROUPS -> {return "List all groups.";}
            case CREATE_GROUP -> {return "Create a social group.";}
            case UPDATE_GROUP -> {return "Update a group's details.";}
            case JOIN_GROUP -> {return "Join a group.";}
            case LEAVE_GROUP -> {return "Leave a group.";}
            case LIST_MEETINGS -> {return "List upcoming meetings of the groups you belong to.";}
            case CREATE_MEETING -> {return "Create a meeting. You'll need a group ID.";}
            case UPDATE_MEETING -> {return "Update a meeting's details.";}
            case CHECK_UPCOMING_MEETINGS -> {return "Check for your upcoming meetings.";}
            default -> {return "";}

        }
    }

    private static void dispatchAction(SocialAction action, Scanner sc){
        switch (action){
            case GENERATE_FEED -> {generateFeed(sc); break;}
            case POST_COMMENT -> {postComment(sc); break;}
            case EDIT_COMMENT -> {editComment(sc); break;}
            case REPLY_TO_COMMENT -> {replyComment(sc); break;}
            case LIKE_COMMENT -> {likeComment(sc); break;}
            case SHARE_COMMENT -> {shareComment(sc); break;}
            case LIST_GROUPS -> {listGroups(); break;}
            case CREATE_GROUP -> {createGroup(sc); break;}
            case UPDATE_GROUP -> {updateGroup(sc); break;}
            case JOIN_GROUP -> {joinGroup(sc); break;}
            case LEAVE_GROUP -> {leaveGroup(sc); break;}
            case LIST_MEETINGS -> {listMeetings(); break;}
            case CREATE_MEETING -> {createMeeting(sc); break;}
            case UPDATE_MEETING -> {updateMeeting(sc); break;}
            case CHECK_UPCOMING_MEETINGS -> {checkUpcomingMeetings(sc); break;}
            default -> {return;}
        }
    }

    private static void generateFeed(Scanner sc){
        System.out.println("Would you like to generate your social feed, or the feed of a specific group?");
        System.out.println("Enter a group ID, or '0' for your personal feed.");
        String id = sc.next();
        List<Interaction> interactions = null;
        if (id == "0"){
            interactions = SocialController.getInteractionsByUserId(UserController.getCurrentUser().getID());
        } else {
            interactions = SocialController.getInteractionsByGroupId(Integer.parseInt(id));
        }
        //Put all interactions in buckets. Filter all the original interactions, put all the child interactions
        // in a map for later retrieval.
        List<Interaction> rootInteractions = new ArrayList<Interaction>();
        Map<Integer, List<Interaction>> childInteractions = new HashMap<>();
        for (Interaction inter : interactions){
            if (inter.getType() == Interaction.Interaction_Type.ORIGINAL_CONTENT){
                rootInteractions.add(inter);
            } else {
                List<Interaction> val = null;
                if (childInteractions.containsKey(inter.getTargetId())){
                    val = childInteractions.get(inter.getTargetId());
                } else {
                    val = new ArrayList<Interaction>();
                }
                val.add(inter);
                childInteractions.put(inter.getTargetId(), val);
            }
        }

        for (Interaction inter : rootInteractions){
            printHelper(inter, childInteractions);
        }


    }

    //Print the interaction, then print all interactions that target this interaction recursively.
    private static void printHelper(Interaction inter, Map<Integer, List<Interaction>> childInteractions){
        User postingUser = UserController.getUserById(inter.getUserId());
        //Print the current interaction.
        switch (inter.getType()){
            case ORIGINAL_CONTENT -> {
                System.out.println("-----");
                System.out.println(postingUser.getFirstName() + " " + postingUser.getLastName() + " posted :");
                System.out.println(inter.getContent());
                System.out.println("Posted on " + inter.getTimestamp().toString());
                break;
            }
            case COMMENT_ON_CONTENT -> {
                System.out.println("---");
                System.out.println(postingUser.getFirstName() + " " + postingUser.getLastName() + " replied :");
                System.out.println(inter.getContent());
                System.out.println("Posted on " + inter.getTimestamp().toString());
                break;
            }
            case LIKE_CONTENT -> {
                System.out.println("---");
                System.out.println(postingUser.getFirstName() + " " + postingUser.getLastName() + " liked this post.");
                System.out.println("Posted on " + inter.getTimestamp().toString());
            }
            case SHARE_ON_CONTENT -> {
                System.out.println("---");
                System.out.println(postingUser.getFirstName() + " " + postingUser.getLastName() + " shared this post.");
                System.out.println("Posted on " + inter.getTimestamp().toString());
            }
        }
        if (childInteractions.containsKey(inter.getID())){
            //Loop through all interactions that targeted the current interaction, and print those recursively depth first.
            for (Interaction childInter : childInteractions.get(inter.getID())){
                printHelper(childInter, childInteractions);
            }
        }
    }

    //Post a new comment, either to the global feed or to a specific group.
    private static void postComment(Scanner sc){
        System.out.println("Please indicate in which group you'd like to post your comment.");
        System.out.println("Enter a group ID, or '0' for a global post. Note that you must be a member of a group to post in that group.");
        String id = sc.next();
        sc.nextLine();
        System.out.println("What would you like to post?");
        String comment = sc.nextLine();
        System.out.println(comment);
        SocialController.postRootComment(UserController.getCurrentUser(), Integer.parseInt(id), comment);
    }

    //Edit a comment by changing it's content. Cannot edit anything else about the comment, ex. no replacing target ID
    private static void editComment(Scanner sc){
        System.out.println("Enter the id of the comment you'd like to edit. You may only edit your own comments.");
        String id = sc.nextLine();
        Interaction existingComment = SocialController.getInteractionById(Integer.parseInt(id));
        if (existingComment == null){
            System.out.println("No comment found with that Id.");
            return;
        }
        if (existingComment.getUserId() != UserController.getCurrentUser().getID()){
            System.out.println("Comment owned by another user. You may only edit your own comments.");
            return;
        }
        System.out.println("The current content of this comment:");
        System.out.println(existingComment.getContent());
        System.out.println("What would you like to replace it with?");
        String comment = sc.nextLine();
        SocialController.editComment(UserController.getCurrentUser(), Integer.parseInt(id), comment);
    }

    //Posts a new reply to a comment.
    private static void replyComment(Scanner sc){
        System.out.println("Enter the id of the comment you're replying to.");
        String id = sc.next();
        sc.nextLine();
        System.out.println("What would you like to post?");
        String comment = sc.nextLine();
        SocialController.postReplyComment(UserController.getCurrentUser(), Integer.parseInt(id), comment);
    }

    //Likes a comment.
    private static void likeComment(Scanner sc){
        System.out.println("Enter the id of the comment you're liking.");
        String id = sc.next();
        SocialController.likeComment(UserController.getCurrentUser(), Integer.parseInt(id));
    }

    //Share a comment.
    private static void shareComment(Scanner sc){
        System.out.println("Enter the id of the comment you'd like to share.");
        String id = sc.next();
        SocialController.shareComment(UserController.getCurrentUser(), Integer.parseInt(id));
    }

    private static void listGroups(){
        List<Group> groups = SocialController.listAllGroups();
        System.out.println("------------------------------------------------------------------------------------------"); //90 dashes
        System.out.println("| Group Id | Group Name                                                                  |");
        System.out.println("------------------------------------------------------------------------------------------");
        for (Group g : groups){
            System.out.println("| " + Utils.fitString(g.getID() + "", 8) +
                    " | " + Utils.fitString(g.getName(), 75) +" |");
        }
        System.out.println("------------------------------------------------------------------------------------------");
    }

    //Create a new group.
    private static void createGroup(Scanner sc){
        System.out.println("What's the name of your new group?");
        String name = sc.nextLine();
        System.out.println("Add a description for this new group.");
        String desc = sc.nextLine();
        SocialController.createNewGroup(UserController.getCurrentUser(), name, desc);
    }

    private static void updateGroup(Scanner sc){
        System.out.println("Enter the group id of the group you'd like to modify. You must own this group to modify it.");
        String id = sc.next();
        sc.nextLine();
        Group curGroup = SocialController.getGroupByGroupId(Integer.parseInt(id));
        System.out.println("The current description of this group:");
        System.out.println(curGroup.getDescription());
        System.out.println("Enter what you'd like to change the group description to, or hit enter to leave it unchanged.");
        String desc = sc.nextLine();
        SocialController.updateGroupWithGroupId(Integer.parseInt(id), UserController.getCurrentUser(), desc);


    }

    private static void joinGroup(Scanner sc){
        System.out.println("Enter the group id of the group you'd like to join.");
        String id = sc.next();
        SocialController.joinGroup(Integer.parseInt(id), UserController.getCurrentUser());
    }

    private static void leaveGroup(Scanner sc){
        System.out.println("Enter the group id of the group you'd like to leave.");
        String id = sc.next();
        SocialController.leaveGroup(Integer.parseInt(id), UserController.getCurrentUser());
    }

    private static void listMeetings(){
        List<Meeting> meetings = SocialController.listAllMeetings();
        System.out.println("------------------------------------------------------------------------------------------");
        System.out.println("| Meeting Id | Group of Meeting               | Meeting Location        | Meeting Time   |");
        System.out.println("------------------------------------------------------------------------------------------");
        for (Meeting m : meetings){
            Group g = SocialController.getGroupByGroupId(m.getGroupId());
            System.out.println("| " + Utils.fitString(m.getID() + "", 10) +
                    " | " + Utils.fitString(g.getName(), 30) +
                    " | " + Utils.fitString(m.getMeetingLocation(), 23) +
                    " | " + Utils.fitString(m.getMeetingTime().toString(), 14) + " |");
        }
        System.out.println("------------------------------------------------------------------------------------------");
    }

    private static void createMeeting(Scanner sc){
        System.out.println("All meetings are associated with a group. Enter the group id of the group you'd like to create a meeting for.");
        System.out.println("You must be part of the group to create a meeting for the group.");
        String id = sc.next();
        sc.nextLine();
        System.out.println("Where is the meeting location?");
        String location = sc.nextLine();
        System.out.println("What day is the meeting? Use MM/DD/YYYY format.");
        String day = sc.next();
        System.out.println("What time? Use 2400 time notation.");
        String time = sc.next();
        SocialController.createMeeting(Integer.parseInt(id), UserController.getCurrentUser(), location, day, time);
    }
    private static void updateMeeting(Scanner sc){
        System.out.println("Enter the meeting id of the meeting you'd like to modify. You must be part of this group to update its meeting.");
        String id = sc.next();
        sc.nextLine();
        Meeting curMeeting = SocialController.getMeetingByMeetingId(Integer.parseInt(id));
        System.out.println("The current location of this group is '" + curMeeting.getMeetingLocation() + "'.");
        System.out.println("Enter what you'd like to change the location to, or hit enter to leave it unchanged.");
        String location = sc.nextLine();
        System.out.println("The current date of this meeting is " + curMeeting.getMeetingTime() + ".");
        System.out.println("Update the day of the meeting. Use MM/DD/YYYY format.");
        String day = sc.next();
        System.out.println("What time? Use 2400 time notation.");
        String time = sc.next();
        SocialController.updateMeetingWithMeetingId(Integer.parseInt(id), UserController.getCurrentUser(), location, day, time);

    }
    private static void checkUpcomingMeetings(Scanner sc){
        List<Meeting> meetings = SocialController.listAllMeetings(UserController.getCurrentUser());
        System.out.println("------------------------------------------------------------------------------------------");
        System.out.println("| Meeting Id | Group of Meeting               | Meeting Location        | Meeting Time   |");
        System.out.println("------------------------------------------------------------------------------------------");
        for (Meeting m : meetings){
            Group g = SocialController.getGroupByGroupId(m.getGroupId());
            System.out.println("| " + Utils.fitString(m.getID() + "", 10) +
                    " | " + Utils.fitString(g.getName(), 30) +
                    " | " + Utils.fitString(m.getMeetingLocation(), 23) +
                    " | " + Utils.fitString(m.getMeetingTime().toString(), 14) + " |");
        }
        System.out.println("------------------------------------------------------------------------------------------");
    }

}
