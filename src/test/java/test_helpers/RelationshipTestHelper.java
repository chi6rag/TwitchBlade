package test_helpers;

import net.chi6rag.twitchblade.domain.*;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class RelationshipTestHelper {
    DbConnection connection;

    public RelationshipTestHelper(DbConnection connection){
        this.connection = connection;
    }

    public void createSampleFollowersFor(User user){
        User followerOne = (new User("bar_example", "123456789",connection)).save();
        User followerTwo = (new User("baz_example", "123456789",connection)).save();
        try {
            PreparedStatement followerStatement = connection.prepareStatement
                    (
                            "INSERT INTO relationship(follower_id, followed_id) " +
                                    "VALUES(?,?)"
                    );
            followerStatement.setInt(1, followerOne.getId());
            followerStatement.setInt(2, user.getId());
            followerStatement.execute();
            followerStatement.setInt(1, followerTwo.getId());
            followerStatement.setInt(2, user.getId());
            followerStatement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteAllRelationships() {
        try {
            PreparedStatement preparedStatement = this.connection
                    .prepareStatement("DELETE FROM relationship");
            preparedStatement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void createSampleFollowersFor(User user, String... usernames){
        ArrayList<User> followers = new ArrayList<User>();
        for(int i=0; i<usernames.length; i++){
            followers.add((new User(usernames[i], "123456789", connection)).save());
        }
        try {
            PreparedStatement followerStatement = connection.prepareStatement
                (
                    "INSERT INTO relationship(follower_id, followed_id) " +
                    "VALUES(?,?)"
                );
            for(int i=0; i<followers.size(); i++){
                followerStatement.setInt(1, followers.get(i).getId());
                followerStatement.setInt(2, user.getId());
                followerStatement.execute();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void validateFollowers(User user, ArrayList<User> followers){
        String isFollowingErrorMessage;
        assertEquals(followers.getClass().getSimpleName(), "ArrayList");
        for(int i=0; i<followers.size(); i++){
            assertEquals(followers.get(i).getClass().getSimpleName(), "User");
            isFollowingErrorMessage = "Failure: " + followers.get(i).getUsername() +
                    " is not following " + user.getUsername();
            assertTrue(isFollowingErrorMessage, isFollowing(followers.get(i), user));
        }
    }

    public void validateFollowers(User user, User... followers){
        String isFollowingErrorMessage;
        for(int i=0; i<followers.length; i++){
            assertEquals(followers[i].getClass().getSimpleName(), "User");
            isFollowingErrorMessage = "Failure: " + followers[i].getUsername() +
                    " is not following " + user.getUsername();
            assertTrue(isFollowingErrorMessage, isFollowing(followers[i], user));
        }
    }

    public void validateNonFollower(User followed, User follower){
        String nonFollowerError = follower.getUsername() + " is following " +
                followed.getUsername();
        assertTrue(nonFollowerError, !isFollowing(follower, followed));
    }

    public boolean isFollowing(User follower, User followed){
        boolean isFollowing = false;
        if(follower.getId() == null || followed.getId() == null) return false;
        try {
            PreparedStatement isFollowingStatement = prepareIsFollowingStatement();
            isFollowingStatement.setInt(1, follower.getId());
            isFollowingStatement.setInt(2, followed.getId());
            ResultSet res = isFollowingStatement.executeQuery();
            if(res.next()){ isFollowing = (res.getInt("isFollowing")==1) ?  true : false; }
        } catch (SQLException e) {
            // e.printStackTrace();
        }
        return isFollowing;
    }

    private PreparedStatement prepareIsFollowingStatement() throws SQLException {
        PreparedStatement isFollowingStatement =  null;
            isFollowingStatement = this.connection.prepareStatement(
                "SELECT COUNT(*) AS isFollowing FROM relationship WHERE " +
                "relationship.follower_id=? AND relationship.followed_id=?"
            );
        return isFollowingStatement;
    }

}
