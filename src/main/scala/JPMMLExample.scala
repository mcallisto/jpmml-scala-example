import java.io.File
import java.io.FileInputStream

import org.dmg.pmml.PMML
import org.jpmml.evaluator.Evaluator
import org.jpmml.evaluator.InputField
import org.jpmml.evaluator.ModelEvaluatorFactory

import org.jpmml.model.PMMLUtil

import collection.JavaConverters._

import com.github.tototoshi.csv._

object JPMMLExample extends App {
  val pmml_filepath = "data/single_iris_logreg.xml"
  val input_csv_filename = "data/Iris.csv"
  val output_csv_filename = "data/Iris_scored.csv"

  // Set up the PMML evaluator
  val evaluator: Evaluator = prepModelEvaluator(readPMML(pmml_filepath))

  // Read the CSV file
  val csv_list: List[Map[String, String]] = readCSV(input_csv_filename)

  // Prepare predictor fields
  val scoring_list = csv_list.map(row ⇒ {
    val activeFields: List[InputField] = evaluator.getActiveFields.asScala.toList
    activeFields.map(field ⇒ {
      val fieldName = field.getName
      val fieldValue = field.prepare(row(fieldName.toString))
      fieldName → fieldValue
    }).toMap.asJava
  })

  // Perform scoring using JPMML
  val regular_score_list = scoring_list.map(el ⇒ evaluator.evaluate(el).values.asScala.toList)

  // Save the outputs as a CSV
  writeCSV(output_csv_filename, regular_score_list)

  // Reads the PMML File and returns a PMML object
  def readPMML(filename: String): PMML = {
    val is = new FileInputStream(new File(filename))
    try {
      PMMLUtil.unmarshal(is)
    }
    finally {
      is.close()
    }
  }

  // Prepares the model evaluator
  // (note that it returns Evaluator and not ModelEvaluator)
  def prepModelEvaluator(pmml: PMML): Evaluator = {
    ModelEvaluatorFactory.newInstance().newModelEvaluator(pmml)
  }

  // Reads CSV with headers
  def readCSV(filename: String): List[Map[String, String]] = {
    val reader = CSVReader.open(new File(filename))
    reader.allWithHeaders()
  }

  // Write a CSV file
  def writeCSV(filename: String, output_list: List[List[Any]]): Unit = {
    val f = new File(filename)
    val writer = CSVWriter.open(f)
    writer.writeAll(output_list)
    writer.close
  }
}