package com.wonkglorg.ytdlp;

import java.util.Map;
import java.util.Objects;

/**
 * YtDlp response
 */
public class YtDlpResponse {

    private Map<String, String> options;
    private String command;
    private int exitCode;
    private String out;
    private String err;
    private String directory;
    private int elapsedTime;

    public YtDlpResponse(
            String command,
            Map<String, String> options,
            String directory,
            int exitCode,
            int elapsedTime,
            String out,
            String err) {
        this.command = command;
        this.options = options;
        this.directory = directory;
        this.elapsedTime = elapsedTime;
        this.exitCode = exitCode;
        this.out = out;
        this.err = err;
    }

    public String getCommand() {
        return command;
    }

    public int getExitCode() {
        return exitCode;
    }

    public String getOut() {
        return out;
    }

    public String getErr() {
        return err;
    }

    public Map<String, String> getOptions() {
        return options;
    }

    public String getDirectory() {
        return directory;
    }

    public int getElapsedTime() {
        return elapsedTime;
    }

    @Override
    public String toString() {
        return "YtDlpResponse{" +
                "options=" + options +
                ", command='" + command + '\'' +
                ", exitCode=" + exitCode +
                ", out='" + out + '\'' +
                ", err='" + err + '\'' +
                ", directory='" + directory + '\'' +
                ", elapsedTime=" + elapsedTime +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof YtDlpResponse that)) return false;
        return exitCode == that.exitCode && elapsedTime == that.elapsedTime && Objects.equals(options, that.options) && Objects.equals(command, that.command) && Objects.equals(out, that.out) && Objects.equals(err, that.err) && Objects.equals(directory, that.directory);
    }

    @Override
    public int hashCode() {
        return Objects.hash(options, command, exitCode, out, err, directory, elapsedTime);
    }
}
