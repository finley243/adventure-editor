package com.github.finley243.adventureeditor.ui;

import com.github.finley243.adventureeditor.ProjectFile;

import java.util.List;

public interface RecentProjectListener {

    void onUpdateRecentProjects(List<ProjectFile> recentProjects);

}
