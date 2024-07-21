package lib.model;

/**
 * A join table class that indicates that a user is part of a group.
 *
 */
public class GroupMember implements Has_ID, Has_Copy<GroupMember> {
    private int Id;
    private int User_Id;
    private int Group_Id;

    protected GroupMember() {
    	this.Id = -1;
    	this.User_Id = -1;
    	this.Group_Id = -1;
    }

  

    @Override
    public int getID() { return Id;}

    @Override
    public void setID(int Id) {this.Id = Id;}

    public int getUserId() { return this.User_Id;}

    public void setUserId(int userId) {this.User_Id = userId;}

    public int getGroupId() { return this.Group_Id; }

    public void setGroupId(int groupId) {this.Group_Id = groupId; }


    
    public static GroupMember create(int id, int userId, int groupId) {
        GroupMember member = new GroupMember();
        member.setID(id);
        member.setGroupId(groupId);
        member.setUserId(userId);
        return member;
    }
    public static GroupMember create() {
    	return new GroupMember();
    }
    public static GroupMember copy(GroupMember groupMember) {
    	return GroupMember.create(groupMember.getID(), groupMember.getUserId(), groupMember.getGroupId());
    }
    public GroupMember copy() {
    	return GroupMember.copy(this);
    }
}