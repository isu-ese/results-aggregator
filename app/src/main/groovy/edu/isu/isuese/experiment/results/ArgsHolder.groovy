/*
 * The MIT License (MIT)
 *
 * ISUESE Arc Framework Results Aggregator
 * Copyright (c) 2017-2021 Idaho State University Empirical Software Engineering
 * Laboratory
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package edu.isu.isuese.experiment.results

import groovy.cli.Option
import groovy.cli.Unparsed

/**
 * @author Isaac D Griffith
 * @version 1.3.0
 */
class ArgsHolder {

    @Option(shortName='h', description='display usage')
    Boolean help

    @Option(shortName='o', description='output file name')
    String output

    @Option(shortName='c', description='configuration file')
    String config

    String base

    @Unparsed(description = 'directory to be processed')
    void setBase(dirs) {
        if (dirs) base = dirs.first()
        else base = "."
    }

    String getBase() { base }
}
