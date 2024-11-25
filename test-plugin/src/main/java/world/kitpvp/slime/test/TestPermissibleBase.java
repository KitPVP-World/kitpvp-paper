package world.kitpvp.slime.test;

import org.bukkit.permissions.PermissibleBase;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.ServerOperator;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class TestPermissibleBase extends PermissibleBase {
    private boolean hasAnyPermission = false;

    public TestPermissibleBase(@Nullable ServerOperator opable) {
        super(opable);
    }

    @Override
    public synchronized void recalculatePermissions() {
        super.recalculatePermissions();
        var permission = this.getPermissions().get("*");
        if(permission != null) {
            this.hasAnyPermission = permission.getValue();
        }
    }

    @Override
    public boolean isOp() {
        return super.isOp();
    }

    @Override
    public boolean hasPermission(@NotNull String inName) {
        return this.hasAnyPermission || super.hasPermission(inName);
    }

    @Override
    public boolean hasPermission(@NotNull Permission perm) {
        return this.hasAnyPermission || super.hasPermission(perm);
    }
}
