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
package io.tessilab.oss.openutils.treedisplaying;

import java.io.PrintStream;

/**
 * Displays a tree like in linux console. An example can be :
 * <p>
 *  ├── root <br/>
 *  │   ├── param1 <br/>
 *  │   │   ├── param2 <br/>
 *  │   │   │   └── param3 <br/>
 *  │   │   └── param4 <br/>
 *  │   ├── param5 <br/>
 *  │   │   └── param6 <br/>
 * <p>
 * The classes to display must implement {@link io.tessilab.oss.openutils.treedisplaying.TreeStructure}
 * @author Andres BEL ALONSO
 */
public class TreeLinuxConsoleDisplay implements TreeDisplayer{
    
    private final PrintStream outStream;

    public TreeLinuxConsoleDisplay() {
        this.outStream = System.out;
    }
    
    public TreeLinuxConsoleDisplay(PrintStream outputStream) {
        this.outStream = outputStream;
    }
    

    @Override
    public void displayTree(TreeStructure tree) {
        this.print("",tree);
    }
    
    private void print(String prefix, TreeStructure tree) {
        outStream.println(prefix + (tree.isLeaf() ? "└── " : "├── ") + tree.getNodeName());
        for (int i = 0; i < tree.getChilds().size() - 1; i++) {
            print(prefix + (tree.isLeaf() ? "    " : "│   "),tree.getChilds().get(i));
        }
        if (!tree.getChilds().isEmpty()) {
            print(prefix + (tree.isLeaf() ?"    " : "│   "), tree.getChilds().get(tree.getChilds().size() - 1));
        }
    }
    
}
