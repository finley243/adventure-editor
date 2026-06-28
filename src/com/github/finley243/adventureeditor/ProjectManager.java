package com.github.finley243.adventureeditor;

import java.util.ArrayList;
import java.util.List;

public class ProjectManager {

    private static final int RECENT_PROJECTS_MAXIMUM = 5;

    private final List<ProjectFile> recentProjects;

    private boolean isProjectLoaded;
    private String loadedProjectPath;

    public ProjectManager() {
        this.recentProjects = new ArrayList<>();
        this.isProjectLoaded = false;
        this.loadedProjectPath = null;
    }

    public void setProjectLoaded(boolean loaded) {
        this.isProjectLoaded = loaded;
    }

    public boolean hasUnsavedChanges() {
        if (!isProjectLoaded) {
            return false;
        }
        return loadedProjectPath == null;
    }

    public void addRecentProject(ProjectFile project) {
        recentProjects.remove(project);
        recentProjects.addFirst(project);
        while (recentProjects.size() > RECENT_PROJECTS_MAXIMUM) {
            recentProjects.removeLast();
        }
    }

    public void loadRecentProjects(List<ProjectFile> projects) {
        recentProjects.clear();
        recentProjects.addAll(projects);
        while (recentProjects.size() > RECENT_PROJECTS_MAXIMUM) {
            recentProjects.removeLast();
        }
    }

    public List<ProjectFile> getRecentProjects() {
        return new ArrayList<>(recentProjects);
    }

    public void removeRecentProject(ProjectFile project) {
        recentProjects.remove(project);
    }

    public void clearRecentProjects() {
        recentProjects.clear();
    }

    public void setLoadedProjectPath(String path) {
        this.loadedProjectPath = path;
    }

    public String getLoadedProjectPath() {
        return loadedProjectPath;
    }

}
