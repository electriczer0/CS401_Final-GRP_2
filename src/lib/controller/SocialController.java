package lib.controller;

import lib.model.Group;
import lib.model.Interaction;
import lib.model.Meeting;
import lib.model.User;

import java.util.ArrayList;
import java.util.List;

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
        return new ArrayList<Interaction>();
    }

    /**
     * Based on a group Id, returns the interactions for that group, sorted by timestamp.
     * @param groupId
     * @return
     */
    public static List<Interaction> getInteractionsByGroupId(int groupId){
        return new ArrayList<Interaction>();
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
        return;
    }

    /**
     * Looks up and returns an interaction by ID.
     * @param interactionId
     * @return
     */
    public static Interaction getInteractionById(int interactionId){
        return Interaction.create();
    }

    /**
     * Replaces the content of an existing comment with new content. The user must be the owner of the interaction
     * (i.e. the only comments the user can edit are their own), and the interaction must actually have content
     * (it must be either InteractionType.ORIGINAL_CONTENT or InteractionType.COMMENT_ON_CONTENT
     * @param interactionId
     * @param newComment
     */
    public static void editComment(User user, int interactionId, String newComment){
        return;
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
        return;
    }

    /**
     * Creates a like interaction for a comment. There should be no restriction on anyone liking anyone else's content, but
     * the target interaction must be either InteractionType.ORIGINAL_CONTENT or InteractionType.COMMENT_ON_CONTENT
     * so we don't get people liking each other's likes.
     * @param user
     * @param interactionId
     */
    public static void likeComment(User user, int interactionId){
        return;
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
        return;
    }

    /**
     * Creates a new group. This group is owned by the creating user and has a name, type, and description.
     * @param user
     * @param name
     * @param description
     */
    public static void createNewGroup(User user, String name, String description){
        return;
    }

    /**
     * Returns a list of all groups.
     * @return
     */
    public static List<Group> listAllGroups(){
        return new ArrayList<Group>();
    }

    /**
     * Retrieves a group by id, or null if it doesn't exist.
     * @param id
     * @return
     */
    public static Group getGroupByGroupId(int id){
        return Group.create();
    }

    /**
     * Retrieves a meeting by id, or null if it doesn't exist.
     * @param id
     * @return
     */
    public static Meeting getMeetingByMeetingId(int id){
        return Meeting.create();
    }

    /**
     * Updates the group with the targeted id by setting a new description, then re-saving the record.
     * Does *not* update the field if the argument is null or empty.
     * Reject the update if the user does not own the group (which means they dont have permissions to modify it).
     * @param id
     * @param description
     */
    public static void updateGroupWithGroupId(int id, User user, String description){

    }

    /**
     * Adds the user to the group. No effect if they're already in the group.
     * @param groupId
     * @param user
     */
    public static void joinGroup(int groupId, User user){

    }

    /**
     * Removes the user from the group. No effect if they weren't in the group already.
     * @param groupId
     * @param user
     */
    public static void leaveGroup(int groupId, User user){

    }

    /**
     * Returns a list of all meetings, sorted chronologically.
     * @return
     */
    public static List<Meeting> listAllMeetings(){
        return new ArrayList<Meeting>();
    }

    /**
     * Returns a list of all meetings for the provided user, i.e. all meetings for the groups this user is a member of.
     * @return
     */
    public static List<Meeting> listAllMeetings(User user){
        return new ArrayList<Meeting>();
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

    }
}