package org.edx.mobile.task;

import android.content.Context;
import android.support.annotation.NonNull;

import org.edx.mobile.discussion.DiscussionThread;

public abstract class SetThreadVotedTask extends Task<DiscussionThread> {
    private final DiscussionThread thread;
    private final boolean voted;

    public SetThreadVotedTask(@NonNull Context context,
                              @NonNull DiscussionThread thread, boolean voted) {
        super(context);
        this.thread = thread;
        this.voted = voted;
    }

    public DiscussionThread call() throws Exception {
        return environment.getDiscussionAPI().setThreadVoted(thread, voted);
    }
}
