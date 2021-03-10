package com.presentations.design_patterns.object_oriented_design.good_design;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Issue {

    List<System> systems;
    private int id;

    public Issue(List<System> systems) {
        this.systems = systems;
    }

    public int getId() {
        return this.id;
    }

    public List<System> getSystems() {
        return this.systems;
    }
}

public class System {

    private String name;
    private int buildNumber;

    public System(String name, int buildNumber) {
        this.name = name;
        this.buildNumber = buildNumber;
    }

    public String getName() {
        return this.name;
    }

    public int getBuildNumber() {
        return this.buildNumber;
    }

    public void setBuildNumber(int buildNumber) {
        this.buildNumber = buildNumber;
    }
}

public class NullUser extends User {

    @Override
    public List<Issue> getIssues() {
        return new ArrayList<Issue>();
    }

    @Override
    public boolean isNull() {
        return true;
    }

    public NullUser() {
        super(0);
    }

    @Override
    public boolean assignTo(User assignee, Issue issue) {
        return false;
    }

    @Override
    public System getFirstSystem(int issueId, int buildNumber) {
        return new NullSystem();
    }
}

public class NullSystem extends System {

    public NullSystem(String name, int buildNumber) {
        super(name, buildNumber);
    }
}


public class User {

    private static List<Issue> ISSUES;
    private int id;

    public User(int id) {
        this.id = id;
    }

    public int getId() {
        return this.id;
    }

    public List<Issue> getIssues() {
        return this.ISSUES;
    }

    public boolean assignTo(User assignee, Issue issue) {
        // Assign issue to assignee
        // If successful, then remove issue from current User
        // and return true
        // otherwise, return false
    }

    public System getFirstSystem(int issueId, int buildNumber) {
        Optional<Issue> issue = this.getIssues().stream()
            .filter(item -> item.getId() == issueId)
            .findFirst();

        if (!issue.isPresent()) {
            return null;
        }

        List<System> systems = new ArrayList<>();
        issue.ifPresent(item -> systems.addAll(item.getSystems()));

        Optional<System> result = systems.stream().findFirst().map(system -> {
            system.setBuildNumber(buildNumber);
            return system;
        });

        return result.orElse(null);
    }

    public System getFirstSystemWithoutSideEffect(int issueId, int buildNumber) {
        Optional<Issue> issue = this.getIssues().stream()
            .filter(item -> item.getId() == issueId)
            .findFirst();

        if (!issue.isPresent()) {
            return null;
        }

        List<System> systems = new ArrayList<>();
        issue.ifPresent(item -> systems.addAll(item.getSystems()));

        Optional<System> result = systems.stream().findFirst().map(system -> {
            return new System(system.getName(), buildNumber);
        });

        return result.orElse(null);
    }

    public boolean getBankLoad(int cash, String name) {
        return name.equals("Ahmed") && cash > 0 || cash > 600;
    }

    public boolean getBankLoanWithGuards(int cash, String name) {
        if (cash <= 0) {
            return false;
        }

        if (!name.equals("Ahmed") && cash <= 600) {
            return false;
        }

        // you are Ahmed! and Ahmed has lots of bank reserves

        return true;
    }
}

