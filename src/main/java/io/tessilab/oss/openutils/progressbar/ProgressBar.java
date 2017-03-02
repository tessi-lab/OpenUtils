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
import java.time.LocalDateTime;

/**
 * A class to interface to allow a different use of ProgressBar project of Tongfei Chen
 * avaible at : 
 * <p>
 *  https://github.com/ctongfei/progressbar
 * 
 * @author Andres BEL ALONSO
 */
public class ProgressBar {
    
    private final ProgressThread progressThread;
    private final ProgressState progressState;
    
    public ProgressBar(ProgressBarStyle style, PrintStream printStream, long maxState, String barName) {
        progressState = new ProgressState(barName, maxState);
        progressThread = new ProgressThread(progressState, style, 100000, printStream);
        progressState.startTime = LocalDateTime.now();
    }
    
    /**
     * Increase the internal counter of the progress bar BUT the bar display 
     * is not refresh.
     * @param val The number of values to advance
     */
    public void stepBy(long val) {
        progressState.stepBy(val);
    }
    
    /**
     * Refresh the progress bar.
     */
    public void refreshBar() {
        progressThread.refresh();
    }
}
