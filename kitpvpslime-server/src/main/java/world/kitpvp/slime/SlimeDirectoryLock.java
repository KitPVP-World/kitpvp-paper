package world.kitpvp.slime;

import net.minecraft.util.DirectoryLock;

import java.io.IOException;
import java.nio.file.Path;

public class SlimeDirectoryLock extends DirectoryLock {
    private static final SlimeDirectoryLock INSTANCE = new SlimeDirectoryLock();

    public static DirectoryLock create(Path path, boolean doNotCreateLockFile) throws IOException {
        if(doNotCreateLockFile) {
            return INSTANCE;
        }

        return DirectoryLock.create(path);
    }


    protected SlimeDirectoryLock() {
        //noinspection DataFlowIssue // ignore nullability
        super(null, null);
    }

    @Override
    public void close() {
    }

    @Override
    public boolean isValid() {
        return true;
    }
}
