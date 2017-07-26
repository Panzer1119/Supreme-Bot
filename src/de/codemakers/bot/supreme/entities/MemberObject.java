package de.codemakers.bot.supreme.entities;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 * MemberObject
 *
 * @author Panzer1119
 */
public class MemberObject {

    public static final ArrayList<MemberObject> MEMBEROBJECTS = new ArrayList<>();

    private final HashMap<Object, Object> data = new HashMap<>();
    private final ArrayList<AdvancedMember> members = new ArrayList<>();

    public MemberObject(AdvancedMember... members) {
        this(null, members);
    }

    public MemberObject(HashMap<Object, Object> data, AdvancedMember... members) {
        setData(data);
        setMembers(Arrays.asList(members));
        register();
    }

    public MemberObject(HashMap<Object, Object> data, List<AdvancedMember> members) {
        setData(data);
        setMembers(members);
        register();
    }

    public final HashMap<Object, Object> getData() {
        return data;
    }
    
    public final Object getData(Object key) {
        return data.get(key);
    }

    public final MemberObject setData(HashMap<Object, Object> data) {
        if (data == null) {
            return this;
        }
        this.data.clear();
        this.data.putAll(data);
        return this;
    }

    public final MemberObject putData(Object key, Object value) {
        this.data.put(key, value);
        return this;
    }

    public final MemberObject removeData(Object key) {
        this.data.remove(key);
        return this;
    }

    public final List<AdvancedMember> getMembers() {
        return members;
    }

    public final MemberObject setMembers(List<AdvancedMember> members) {
        if (members == null) {
            return this;
        }
        this.members.clear();
        this.members.addAll(members);
        return this;
    }

    public final MemberObject addMembers(AdvancedMember... members) {
        if (members == null || members.length == 0) {
            return this;
        }
        this.members.addAll(Arrays.asList(members));
        return this;
    }

    public final MemberObject removeMembers(AdvancedMember... members) {
        if (members == null || members.length == 0) {
            return this;
        }
        this.members.removeAll(Arrays.asList(members));
        return this;
    }

    public final boolean register() {
        if (MEMBEROBJECTS.contains(this)) {
            return false;
        }
        return MEMBEROBJECTS.add(this);
    }

    public final boolean unregister() {
        if (!MEMBEROBJECTS.contains(this)) {
            return false;
        }
        return MEMBEROBJECTS.remove(this);
    }

    public final MemberObject delete() {
        deleteData();
        deleteMembers();
        return this;
    }

    public final MemberObject deleteData() {
        data.clear();
        return this;
    }

    public final MemberObject deleteMembers() {
        members.clear();
        return this;
    }

    public static final List<MemberObject> getMemberObjectsByMembers(AdvancedMember... members) {
        if (members == null || members.length == 0) {
            return MEMBEROBJECTS;
        }
        final List<AdvancedMember> members_list = Arrays.asList(members);
        return MEMBEROBJECTS.stream().filter((memberObject) -> memberObject.getMembers().containsAll(members_list)).collect(Collectors.toList());
    }

    public static final MemberObject getMemberObjectByMembers(AdvancedMember... members) {
        return getMemberObjectsByMembers(members).stream().findFirst().orElse(null);
    }

    public static final List<MemberObject> getMemberObjectsByExactMembers(AdvancedMember... members) {
        if (members == null || members.length == 0) {
            return MEMBEROBJECTS;
        }
        final List<AdvancedMember> members_list = Arrays.asList(members);
        return MEMBEROBJECTS.stream().filter((memberObject) -> (memberObject.getMembers().size() == members.length && memberObject.getMembers().containsAll(members_list))).collect(Collectors.toList());
    }

    public static final MemberObject getMemberObjectByExactMembers(AdvancedMember... members) {
        return getMemberObjectsByExactMembers(members).stream().findFirst().orElse(null);
    }

}
