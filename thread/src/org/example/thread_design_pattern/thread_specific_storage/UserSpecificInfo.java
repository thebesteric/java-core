package org.example.thread_design_pattern.thread_specific_storage;

public class UserSpecificInfo {

    private int id;
    private String name;
    private String address;

    private final ThreadLocal<UserSpecificInfo> info = ThreadLocal.withInitial(UserSpecificInfo::new);

    public UserSpecificInfo getUserInfo() {
        return info.get();
    }

    public void setId(int id) {
        getUserInfo().id = id;
    }

    public void setName(String name) {
        getUserInfo().name = name;
    }

    public void setAddress(String address) {
        getUserInfo().address = address;
    }

    public int getId() {
        return getUserInfo().id;
    }

    public String getName() {
        return getUserInfo().name;
    }

    public String getAddress() {
        return getUserInfo().address;
    }

}
