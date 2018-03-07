/*
 * Copyright 2017 Tessi lab.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.tessilab.oss.openutils.progressbar;

import java.io.PrintStream;
import java.time.Duration;
import java.time.LocalDateTime;
import jline.TerminalFactory;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

/**
 * @author Tongfei Chen
 * @since 0.5.0
 */
public class ProgressThread implements Runnable {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(ProgressThread.class);


    volatile boolean running;
    ProgressBarStyle style;
    ProgressState progress;
    long updateInterval;
    PrintStream consoleStream;

    static int consoleRightMargin = 3;

    int length;

    public ProgressThread(ProgressState progress, ProgressBarStyle style, long updateInterval, PrintStream consoleStream) {
        this.progress = progress;
        this.style = style;
        this.updateInterval = updateInterval;
        this.consoleStream = consoleStream;
    }

    // between 0 and 1
    double progress() {
        if (progress.max == 0l) return 0.0;
        else return ((double)progress.current) / progress.max;
    }

    // Number of full blocks
    int progressIntegralPart() {
        return (int)(progress() * length);
    }

    int progressFractionalPart() {
        double p = progress() * length;
        double fraction = (p - Math.floor(p)) * style.fractionSymbols.length();
        return (int) Math.floor(fraction);
    }

    String eta(Duration elapsed) {
        if (progress.max == 0) return "?";
        else if (progress.current == 0) return "?";
        else return Util.formatDuration(
                elapsed.dividedBy(progress.current)
                        .multipliedBy(progress.max - progress.current));
    }

    String percentage() {
        String res;
        if (progress.max == 0) res = "? %";
        else res = String.valueOf((int) Math.floor(100.0 * progress.current / progress.max)) + "%";
        return Util.repeat(' ', 4 - res.length()) + res;
    }

    String ratio() {
        String m = String.valueOf(progress.max);
        String c = String.valueOf(progress.current);
        return Util.repeat(' ', m.length() - c.length()) + c + "/" + m;
    }

    int consoleWidth() {
        return TerminalFactory.get().getWidth();
    }

    void refresh() {
        consoleStream.print('\r');
        LocalDateTime currTime = LocalDateTime.now();
        Duration elapsed = Duration.between(progress.startTime, currTime);

        String prefix = progress.task + " " + percentage() + " " + style.leftBracket;

        int maxSuffixLength = consoleWidth() - consoleRightMargin - prefix.length() - 10;
        String suffix = style.rightBracket + " " + ratio() + " (" + Util.formatDuration(elapsed) + " / " + eta(elapsed) + ") " + progress.extraMessage;
        if (suffix.length() > maxSuffixLength) suffix = suffix.substring(0, maxSuffixLength);

        length = consoleWidth() - consoleRightMargin - prefix.length() - suffix.length();

        StringBuilder sb = new StringBuilder();
        sb.append(prefix);
        sb.append(Util.repeat(style.block, progressIntegralPart()));
        if (progress.current < progress.max) {
            sb.append(style.fractionSymbols.charAt(progressFractionalPart()));
            sb.append(Util.repeat(' ', length - progressIntegralPart() - 1));
        }
        sb.append(suffix);
        String line = sb.toString();

        consoleStream.print(line);
    }

    void kill() {
        running = false;
    }

    public void run() {
        running = true;
        try {
            while (running) {
                refresh();
                Thread.sleep(updateInterval);
            }
            refresh();
            // do-while loop not right: must force to refresh after stopped
        } catch (InterruptedException ex) { }
    }
}