package lib.model;

/**
 * A join table class that indicates that a user is part of a group.
 *
 */
public class GroupMember implements Has_ID {
    private Integer Id;
    private Integer User_Id;
    private Integer Group_Id;

    protected GroupMember() {
    }

  

    @Override
    public int getID() {
        return Id;
    }

    @Override
    public void setID(int Id) {
        this.Id = Id;
    }

    public int getUserId() {
        return this.User_Id;
    }

    public void setUserId(int userId) {
        this.User_Id = userId;
    }

    public int getGroupId() {
        return this.Group_Id;
    }

    public void setGroupId(int groupId) {
        this.Group_Id = groupId;
    }


    
    public static GroupMember create(int id, int userId, int groupId) {
        GroupMember member = new GroupMember();
        member.setID(id);
        member.setGroupId(groupId);
        member.setUserId(userId);
        return member;
    }
}