package raven.mongodb.repository.enumerate;

/**
 * @info create by gelian on 2018/1/8 20:01
 */
public enum UserType {
    Normal(1,"普通用户"),
    VIP(2,"VIP"),
    SuperVIP(3,"超级VIP"),
    GM(99,"管理员");

    UserType(int code,String description){
        this.code = code;
        this.description = description;
    }
    private int code;
    private String description;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
