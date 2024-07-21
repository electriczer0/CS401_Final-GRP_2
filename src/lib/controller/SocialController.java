package lib.controller;

import lib.model.Interaction;
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
     * @param type
     * @param description
     */
    public static void createNewGroup(User user, String name, String type, String description){
        return;
    }
}