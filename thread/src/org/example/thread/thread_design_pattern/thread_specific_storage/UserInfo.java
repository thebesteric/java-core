package org.example.thread.thread_design_pattern.thread_specific_storage;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class UserInfo {
    private Map<String, Object> info = new HashMap<>();

    public void setId(int id) {
        info.put("id", id);
    }

    public void setName(String name) {
        info.put("name", name);
    }

    public void setAddress(String address) {
        info.put("address", address);
    }

    public int getId() {
        return (int) info.get("id");
    }

    public String getName() {
        return (String) info.get("name");
    }

    public String getAddress() {
        return (String) info.get("address");
    }
}
