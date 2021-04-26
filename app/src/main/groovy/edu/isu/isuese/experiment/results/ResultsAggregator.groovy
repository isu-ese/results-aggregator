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

import com.google.common.collect.HashBasedTable
import com.google.common.collect.Table
import groovy.util.logging.Log4j2
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVPrinter
import org.apache.commons.csv.CSVRecord

@Log4j2
class ResultsAggregator {

    List<File> files = []
    Map<String, List<Table<String, String, String>>> data = [:]
    ConfigObject config

    ResultsAggregator(ConfigObject config) {
        this.config = config
    }

    void execute(String path, String output) {
        findFiles(path)
        combineResults()
        writeCombinedResults(output)
    }

    void findFiles(String path) {
        def filePattern = ~/results_\d+_\d+\.csv/
        def directory = new File(path)
        if (!directory.isDirectory()) {
            log.error "The provided directory name ${path} is NOT a directory."
            return
        }

        log.info "Searching for results files in directory ${path}..."

        directory.eachFileRecurse {
            if (filePattern.matcher(it.name).find()) {
                println "File: $it"
                files << it
            }
        }
    }

    void combineResults() {
        log.info "Combining results from all files found"
        files.each {file ->
            Table<String, String, String> table = HashBasedTable.create()
            String replication = file.name.split(/_/)[1]
            println "Replication: $replication"
            Reader reader = new FileReader(file)
            Iterable<CSVRecord> records = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(reader)
            records.each {record ->
                config.headers.each {column ->
                    if (column != "Identifier") {
                        table.put(record.get("Identifier"), column, record.get(column))
                        println("Data: ${record.get("Identifier")}, $column, ${record.get(column)}")
                    }
                }
            }
            reader.close()
            def list = data[replication]
            if (!list)
                data[replication] = [table]
            else
                data[replication] << table
        }
        log.info "Results combined"
    }

    void writeCombinedResults(String file) {
        log.info "Writing combined results to: $file"
        FileWriter out = new FileWriter(file)
        String[] head = ["Replication"] + config.headers
        try (CSVPrinter printer = new CSVPrinter(out, CSVFormat.DEFAULT.withHeader(head))) {
            data.each {rep, list ->
                list.each {table ->
                    table.rowKeySet().each {id ->
                        List<String> rowValues = [rep, id]
                        config.headers.each {
                            if (it != "Identifier") {
                                println "Header: $it"
                                if (table.row(id).containsKey(it))
                                    rowValues << table.get(id, it)
                                else
                                    rowValues << ""
                            }
                        }
//                        rowValues.remove(0)
//                        rowValues.add(0, rep.toString())
                        println rowValues
                        printer.printRecord(rowValues)
                    }
                }
            }
        } catch (IOException ex) {
            log.error ex.getMessage()
        }
        log.info "Finished writing combined results file"
    }
}
