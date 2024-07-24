package lib.controller;

import lib.db.SMGroup_Access;
import lib.db.SMInteraction_Access;
import lib.db.SMMeeting_Access;
import lib.db.User_Access;
import lib.model.Group;
import lib.model.Interaction;
import lib.model.Meeting;
import lib.model.User;

import java.sql.SQLException;
import java.util.*;

/**
 * Controller for all social media interactions.
 */
public class SocialController {

    /**
     * Based on a user's ID, returns a list of interactions for that user.
     * This should get the global interactions and all interactions that belong to groups the user is a part of,
     * then sort that list by timestamp, latest first.
     * @param userId
     * @return
     */
    public static List<Interaction> getInteractionsByUserId(int userId){
        try {
            User_Access userAccessor = User_Access.getInstance();
            SMInteraction_Access smiAccessor = SMInteraction_Access.getInstance();
            //Get the groups this user is in
            Map<Integer, Group> groups = userAccessor.getGroups(UserController.getCurrentUser().getID());
            List<Interaction> totalInteractions = new ArrayList<Interaction>();
            //Loop through those groups and add their interactions to the total list
            for (Group g : groups.values()){
                HashMap<String, String> accessor = new HashMap<>();
                accessor.put("UserID", Integer.toString(g.getID()));
                Map<Integer, Interaction> thisGroupsInteractions = smiAccessor.find(accessor);
                totalInteractions.addAll(thisGroupsInteractions.values());
            }
            //sort the list by timestamp
            Collections.sort(totalInteractions, Comparator.comparing(Interaction::getTimestamp));
            Collections.reverse(totalInteractions);
            //reverse order for descending
            return totalInteractions;

        } catch (SQLException e){
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Based on a group Id, returns the interactions for that group, sorted by timestamp.
     * @param groupId
     * @return
     */
    public static List<Interaction> getInteractionsByGroupId(int groupId){
        try {
            SMInteraction_Access smiAccessor = SMInteraction_Access.getInstance();
            HashMap<String, String> accessor = new HashMap<>();
            accessor.put("GroupID", Integer.toString(groupId));
            Map<Integer, Interaction> thisGroupsInteractions = smiAccessor.find(accessor);
            List<Interaction> interactions = new ArrayList<Interaction>();
            interactions.addAll(thisGroupsInteractions.values());
            Collections.sort(interactions, Comparator.comparing(Interaction::getTimestamp));
            Collections.reverse(interactions);
            return interactions;

        } catch (SQLException e){
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Creates a root comment interaction. The interaction is owned by the User, belongs to the groupId,
     * and is of type InteractionType.ORIGINAL_CONTENT. If the user does not belong to the target groupId,
     * reject the post, as users can only post comments to groups they're members in.
     * @param user
     * @param groupId
     * @param comment
     */
    public static void postRootComment(User user, int groupId, String comment){
        try {
            SMInteraction_Access smiAccessor = SMInteraction_Access.getInstance();
            SMGroup_Access groupAccessor = SMGroup_Access.getInstance();
            //Check if user is in the marked group
            Map<Integer, User> users = groupAccessor.getGroupMembers(groupId);
            if (users.values().contains(user) || groupId == 0){
                Interaction toBeInserted = Interaction.create(
                        -1,
                        user.getID(),
                        groupId,
                        0,
                        Interaction.Interaction_Type.ORIGINAL_CONTENT,
                        comment,
                        new Date());
                smiAccessor.insert(toBeInserted);
            } else {
                //User isnt part of a group, reject the post
                System.out.println("This user is not part of group " + groupId);
            }

        } catch (SQLException e){
            e.printStackTrace();
        }
    }

    /**
     * Looks up and returns an interaction by ID.
     * @param interactionId
     * @return
     */
    public static Interaction getInteractionById(int interactionId){
        try {
            SMInteraction_Access smiAccessor = SMInteraction_Access.getInstance();
            HashMap<String, String> accessor = new HashMap<>();
            accessor.put("InteractionID", Integer.toString(interactionId));
            Map<Integer, Interaction> interaction = smiAccessor.find(accessor);
            if (interaction.values().size() == 1){
                ArrayList<Interaction> tbr = new ArrayList<>();
                tbr.addAll(interaction.values());
                return tbr.get(0); //We enforce id uniqueness so the values should only have one element
            }
        } catch (SQLException e){
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Replaces the content of an existing comment with new content. The user must be the owner of the interaction
     * (i.e. the only comments the user can edit are their own), and the interaction must actually have content
     * (it must be either InteractionType.ORIGINAL_CONTENT or InteractionType.COMMENT_ON_CONTENT
     * @param interactionId
     * @param newComment
     */
    public static void editComment(User user, int interactionId, String newComment){
        try {
            SMInteraction_Access smiAccessor = SMInteraction_Access.getInstance();
            Interaction thisInteraction = getInteractionById(interactionId);
            if (thisInteraction.getUserId() == user.getID() &&
                    (thisInteraction.getType() == Interaction.Interaction_Type.ORIGINAL_CONTENT ||
                            thisInteraction.getType() == Interaction.Interaction_Type.COMMENT_ON_CONTENT)){
                if (newComment.length() > 0){
                    thisInteraction.setContent(newComment);
                    smiAccessor.update(thisInteraction);
                }
            } else {
                System.out.println("User does not own this comment, or interaction is of wrong type.");
            }
        } catch (SQLException e){
            e.printStackTrace();
        }
    }

    /**
     * Creates a comment reply interaction. The interaction is owned by the user but the target interaction may not be.
     * The user must belong to the group of the target interaction, otherwise reject the post. This interaction
     * should be of type InteractionType.COMMENT_ON_CONTENT
     * @param user
     * @param interactionId
     * @param comment
     */
    public static void postReplyComment(User user, int interactionId, String comment){
        try {
            SMInteraction_Access smiAccessor = SMInteraction_Access.getInstance();
            SMGroup_Access groupAccessor = SMGroup_Access.getInstance();
            Interaction parentInteraction = getInteractionById(interactionId);
            //Check if user is in the marked group
            Map<Integer, User> users = groupAccessor.getGroupMembers(parentInteraction.getGroupId());
            if (users.values().contains(user)){
                Interaction toBeInserted = Interaction.create(
                        -1,
                        user.getID(),
                        parentInteraction.getGroupId(),
                        parentInteraction.getID(),
                        Interaction.Interaction_Type.COMMENT_ON_CONTENT,
                        comment,
                        new Date());
                smiAccessor.insert(toBeInserted);
            } else {
                //User isn't part of a group, reject the post
                System.out.println("This user is not part of group " + parentInteraction.getGroupId());
            }

        } catch (SQLException e){
            e.printStackTrace();
        }
    }

    /**
     * Creates a like interaction for a comment. There should be no restriction on anyone liking anyone else's content, but
     * the target interaction must be either InteractionType.ORIGINAL_CONTENT or InteractionType.COMMENT_ON_CONTENT
     * so we don't get people liking each other's likes.
     * @param user
     * @param interactionId
     */
    public static void likeComment(User user, int interactionId){
        try {
            SMInteraction_Access smiAccessor = SMInteraction_Access.getInstance();
            Interaction parentInteraction = getInteractionById(interactionId);
            //Check if user is in the marked group
            Interaction toBeInserted = Interaction.create(
                    -1,
                    user.getID(),
                    parentInteraction.getGroupId(),
                    parentInteraction.getID(),
                    Interaction.Interaction_Type.LIKE_CONTENT,
                    null,
                    new Date());
            smiAccessor.insert(toBeInserted);
        } catch (SQLException e){
            e.printStackTrace();
        }
    }

    /**
     * Creates a share interaction for a comment.
     * The target interaction must be either InteractionType.ORIGINAL_CONTENT or InteractionType.COMMENT_ON_CONTENT,
     * and this function should *also* generate a brand new RootComment that clones the target interaction -
     * "User foobar has shared this comment - " + the original content of the comment that got shared
     *
     * So two interactions should be made here - an interaction indicating something was shared,
     * *and* an interaction that literally shares the original post as a new comment to the global group.
     * @param user
     * @param interactionId
     */
    public static void shareComment(User user, int interactionId){
        try {
            SMInteraction_Access smiAccessor = SMInteraction_Access.getInstance();
            SMGroup_Access groupAccessor = SMGroup_Access.getInstance();
            Interaction parentInteraction = getInteractionById(interactionId);
            //Check if user is in the marked group
            Interaction toBeInserted = Interaction.create(
                    -1,
                    user.getID(),
                    parentInteraction.getGroupId(),
                    parentInteraction.getID(),
                    Interaction.Interaction_Type.SHARE_ON_CONTENT,
                    null,
                    new Date());
            smiAccessor.insert(toBeInserted);

            StringBuilder sb = new StringBuilder();
            sb.append(user.getFirstName() + " shared this comment " + parentInteraction.getID() + ":");
            sb.append(parentInteraction.getContent());

            postRootComment(user, 0, sb.toString());
        } catch (SQLException e){
            e.printStackTrace();
        }
    }

    /**
     * Creates a new group. This group is owned by the creating user and has a name, type, and description.
     * @param user
     * @param name
     * @param description
     */
    public static void createNewGroup(User user, String name, String description){
        try {
            SMGroup_Access groupAccessor = SMGroup_Access.getInstance();
            Group toBeInserted = Group.create(
                    -1,
                    user.getID(),
                    name,
                    description,
                    new Date());
            groupAccessor.insert(toBeInserted);
        } catch (SQLException e){
            e.printStackTrace();
        }
    }

    /**
     * Returns a list of all groups.
     * @return
     */
    public static List<Group> listAllGroups(){
        try {
            SMGroup_Access groupAccessor = SMGroup_Access.getInstance();
            Map<Integer, Group> groups = groupAccessor.readAll();
            return new ArrayList(groups.values());
        } catch (SQLException e){
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Retrieves a group by id, or null if it doesn't exist.
     * @param groupId
     * @return
     */
    public static Group getGroupByGroupId(int groupId){
        try {
            SMGroup_Access groupAccessor = SMGroup_Access.getInstance();
            HashMap<String, String> accessor = new HashMap<>();
            accessor.put("GroupID", Integer.toString(groupId));
            Map<Integer, Group> group = groupAccessor.find(accessor);
            if (group.values().size() == 1){
                ArrayList<Group> tbr = new ArrayList<>(group.values());
                return tbr.get(0); //We enforce id uniqueness so the values should only have one element
            }
        } catch (SQLException e){
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Retrieves a meeting by id, or null if it doesn't exist.
     * @param meetingId
     * @return
     */
    public static Meeting getMeetingByMeetingId(int meetingId){
        try {
            SMMeeting_Access meetingAccessor = SMMeeting_Access.getInstance();
            HashMap<String, String> accessor = new HashMap<>();
            accessor.put("MeetingID", Integer.toString(meetingId));
            Map<Integer, Meeting> meeting = meetingAccessor.find(accessor);
            if (meeting.values().size() == 1){
                ArrayList<Meeting> tbr = new ArrayList<>(meeting.values());
                return tbr.get(0); //We enforce id uniqueness so the values should only have one element
            }
        } catch (SQLException e){
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Updates the group with the targeted id by setting a new description, then re-saving the record.
     * Does *not* update the field if the argument is null or empty.
     * Reject the update if the user does not own the group (which means they dont have permissions to modify it).
     * @param groupId
     * @param description
     */
    public static void updateGroupWithGroupId(int groupId, User user, String description){
        try {
            SMGroup_Access groupAccessor = SMGroup_Access.getInstance();
            Group thisGroup = getGroupByGroupId(groupId);
            if (thisGroup != null && thisGroup.getOwnerId() == user.getID()){
                if (description.length() > 0){
                    thisGroup.setDescription(description);
                    groupAccessor.update(thisGroup);
                }
            } else {
                System.out.println("User does not own this group.");
            }
        } catch (SQLException e){
            e.printStackTrace();
        }
    }

    /**
     * Adds the user to the group. No effect if they're already in the group.
     * @param groupId
     * @param user
     */
    public static void joinGroup(int groupId, User user){
        try {
            SMGroup_Access groupAccessor = SMGroup_Access.getInstance();
            Group thisGroup = getGroupByGroupId(groupId);
            Map<Integer, User> groupUsers = groupAccessor.getGroupMembers(groupId);
            if (thisGroup != null && !groupUsers.values().contains(user)){
                groupAccessor.addUser(user.getID(), groupId);
            } else {
                System.out.println("Group does not exist, or group already contains this user.");
            }
        } catch (SQLException e){
            e.printStackTrace();
        }
    }

    /**
     * Removes the user from the group. No effect if they weren't in the group already.
     * @param groupId
     * @param user
     */
    public static void leaveGroup(int groupId, User user){
        try {
            SMGroup_Access groupAccessor = SMGroup_Access.getInstance();
            Group thisGroup = getGroupByGroupId(groupId);
            Map<Integer, User> groupUsers = groupAccessor.getGroupMembers(groupId);
            if (thisGroup != null && !groupUsers.isEmpty() && groupUsers.values().contains(user)){
                groupAccessor.remUser(user.getID(), groupId);
            } else {
                System.out.println("Group does not exist, or group didn't contain this user.");
            }
        } catch (SQLException e){
            e.printStackTrace();
        }
    }

    /**
     * Returns a list of all meetings, sorted chronologically.
     * @return
     */
    public static List<Meeting> listAllMeetings(){
        try {
            SMMeeting_Access meetingAccessor = SMMeeting_Access.getInstance();
            Map<Integer, Meeting> meetings = meetingAccessor.readAll();
            return new ArrayList(meetings.values());
        } catch (SQLException e){
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Returns a list of all meetings for the provided user, i.e. all meetings for the groups this user is a member of.
     * @return
     */
    public static List<Meeting> listAllMeetings(User user){
        try {
            User_Access userAccessor = User_Access.getInstance();
            Map<Integer, Meeting> meetings = userAccessor.getMeetings(UserController.getCurrentUser().getID());
            return new ArrayList(meetings.values());
        } catch (SQLException e){
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Creates a meeting for the group with id groupId. Sets its location to location, and sets the Date to the
     * Date corresponding to the day (provided in MM/DD/YYYY) and time (2400 format).
     *
     * Rejects creation if the user isn't a member of the group.
     * @param groupId
     * @param location
     * @param day
     * @param time
     */
    public static void createMeeting(int groupId, User user, String location, String day, String time){
        try {
            Group group = getGroupByGroupId(groupId);
            SMMeeting_Access meetingAccessor = SMMeeting_Access.getInstance();
            Date thisDate = new Date();
            String theMonth = day.substring(0, 2);
            String theDay = day.substring(3, 5);
            String theYear = day.substring(6, 10);
            thisDate.setMonth(Integer.parseInt(theMonth) - 1);
            thisDate.setYear(Integer.parseInt(theYear));
            thisDate.setDate(Integer.parseInt(theDay));
            thisDate.setHours(Integer.parseInt(time.substring(0, 1)));
            thisDate.setMinutes(Integer.parseInt(time.substring(2, 3)));

            if (group != null) {
                Meeting toBeInserted = Meeting.create(
                        -1,
                        user.getID(),
                        groupId,
                        group.getName(),
                        group.getDescription(),
                        location,
                        thisDate,
                        new Date());
                meetingAccessor.insert(toBeInserted);
            }
        } catch (SQLException e){
            e.printStackTrace();
        }
    }

    /**
     * Updates a meeting with id meetingId with the updated location and date. Does not update the fields if null,
     * so if the user doesn't provide a day or time, don't update the date.
     *
     * Rejects the update if the user isn't a member of the group the meeting is for.
     *
     * @param meetingId
     * @param user
     * @param location
     * @param day
     * @param time
     */
    public static void updateMeetingWithMeetingId(int meetingId, User user, String location, String day, String time){
        try {
            SMMeeting_Access meetingAccessor = SMMeeting_Access.getInstance();
            Meeting thisMeeting = getMeetingByMeetingId(meetingId);
            if (thisMeeting != null && user.getID() == thisMeeting.getOwnerId()){
                if (location.length() > 0){
                    thisMeeting.setMeetingLocation(location);
                }
                if (day.length() > 0 && time.length() > 0){
                    Date thisDate = new Date();
                    String theMonth = day.substring(0, 2);
                    String theDay = day.substring(3, 5);
                    String theYear = day.substring(6, 10);
                    thisDate.setMonth(Integer.parseInt(theMonth) - 1);
                    thisDate.setYear(Integer.parseInt(theYear));
                    thisDate.setDate(Integer.parseInt(theDay));
                    thisDate.setHours(Integer.parseInt(time.substring(0, 1)));
                    thisDate.setMinutes(Integer.parseInt(time.substring(2, 3)));
                    thisMeeting.setMeetingTimestamp(thisDate);
                }
            }
            meetingAccessor.update(thisMeeting);
        } catch (SQLException e){
            e.printStackTrace();
        }
    }
}