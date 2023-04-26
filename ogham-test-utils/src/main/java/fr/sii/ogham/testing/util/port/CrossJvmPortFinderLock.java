package fr.sii.ogham.testing.util.port;

import java.io.File;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.SortedSet;
import java.util.function.Supplier;

public class CrossJvmPortFinderLock implements PortFinder {
    private final PortFinder delegate;
    private final Path lockFile;

    public CrossJvmPortFinderLock(PortFinder delegate) {
        this.delegate = delegate;
        lockFile = Paths.get(System.getProperty("java.io.tmpdir"), "ogham-port-finder.lock");
    }

    @Override
    public int findAvailablePort(int minPort, int maxPort) {
        return doWithLock(() -> delegate.findAvailablePort(minPort, maxPort));
    }

    @Override
    public SortedSet<Integer> findAvailablePorts(int numRequested, int minPort, int maxPort) {
        return doWithLock(() -> delegate.findAvailablePorts(numRequested, minPort, maxPort));
    }

    private <T> T doWithLock(Supplier<T> func) {
        try (FileChannel channel = FileChannel.open(lockFile, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
             FileLock lock = channel.lock()) {
            return func.get();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
