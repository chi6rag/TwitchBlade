public class UserActivity {
    private User currentUser;

    UserActivity(User currentUser){
        this.currentUser = currentUser;
    }

    public String getActivityOptions(){
        String activityOptions =
                "  Welcome foo_example" +
                        "\n\n"                  +
                        "1. Tweet\n"            +
                        "2. Your Timeline\n"    +
                        "Choose: ";
        return activityOptions;
    }

}