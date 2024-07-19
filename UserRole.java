package biblioConnect_v3;

import java.util.EnumSet;
import java.util.Set;

public enum UserRole {
    STUDENT(EnumSet.of(Permission.BORROW_BOOK, Permission.RETURN_BOOK, Permission.UPDATE_PROFILE)),
    FACULTY(EnumSet.of(Permission.BORROW_BOOK, Permission.RETURN_BOOK, Permission.UPDATE_PROFILE)),
    LIBRARIAN(EnumSet.allOf(Permission.class));

    private final Set<Permission> permissions;

    UserRole(Set<Permission> permissions) {
        this.permissions = permissions;
    }

    public Set<Permission> getPermissions() {
        return permissions;
    }
}