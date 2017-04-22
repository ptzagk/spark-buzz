package com.randrr.buzz.spark

import java.io.{BufferedWriter, OutputStreamWriter}
import java.net.URI

import org.apache.commons.csv.{CSVFormat, CSVPrinter}
import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.fs.{FileSystem, Path}
import org.apache.spark.sql.ForeachWriter

class WordCountWriter(rootDir: String) extends ForeachWriter[TextCount] {
  var writer: BufferedWriter = null
  var printer: CSVPrinter = null

  def open(partitionId: Long, version: Long): Boolean = {
    val fileName = s"$version-$partitionId.json"
    val path = new Path(rootDir, fileName)
    val uri = path.toUri()
    val conf = new Configuration()
    val fs = FileSystem.get(uri, conf)
    val writer = new BufferedWriter(new OutputStreamWriter(fs.create(path)))

    printer = new CSVPrinter(writer, CSVFormat.EXCEL)

    true
  }

  def process(record: TextCount): Unit = {
     printer.print(record.text) 
     printer.print(record.num) 
     printer.println()
  }

  def close(errorNull: Throwable) = {
    printer.flush()
    printer.close()
  }
}
