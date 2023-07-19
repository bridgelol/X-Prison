package dev.drawethree.xprison.gangs.model;

import dev.drawethree.xprison.gangs.managers.GangsManager;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public final class GangTopByMoneyProvider implements GangTopProvider {

	private final GangsManager manager;

	public GangTopByMoneyProvider(GangsManager manager) {
		this.manager = manager;
	}

	@Override
	public List<Gang> provide() {
		return getAllGangs().stream().sorted(Comparator.comparingDouble(Gang::getMoney).reversed()).collect(Collectors.toList());
	}

	private Collection<Gang> getAllGangs() {
		return manager.getAllGangs();
	}
}
