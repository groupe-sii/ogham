package fr.sii.ogham.test.classpath.runner.common;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ParallelProjectsCreator<P, D> implements ProjectsCreator<P, D> {
	private final SingleProjectCreator<P, D> projectCreator;
	private final int numThreads;

	@Override
	public List<String> createProjects(Path parentFolder, boolean override, List<P> expandedMatrix, List<D> exclude) throws SingleProjectCreationException {
		List<Future<String>> futures = new ArrayList<>();
		CompletionService<String> service = new ExecutorCompletionService<>(Executors.newFixedThreadPool(numThreads));
		for (final P params : expandedMatrix) {
			service.submit(() -> projectCreator.createProject(parentFolder, override, params, exclude));
		}
		try {
			for (int i = 0; i < expandedMatrix.size(); i++) {
				futures.add(service.take());
			}
			List<String> modules = new ArrayList<>();
			for (Future<String> future : futures) {
				modules.add(future.get());
			}
			return modules;
		} catch (ExecutionException | InterruptedException e) {
			throw new SingleProjectCreationException("Failed to generate project", e);
		}
	}

}
