package agh.idec.oop.utils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CSVWriter {
    File file;
    FileWriter writer;

    public CSVWriter(File file) throws IOException {
        this.file = file;
        this.writer = new FileWriter(file);
    }

    /**
     * Write line to csv file using given data. Data will be seperated by comma.
     *
     * @param data List of values.
     */
    public void writeData(List<String> data) throws IOException {
        int i = 0;
        for (; i < data.size() - 1; i++) {
            this.writer.write(data.get(i) + ",");
        }
        this.writer.write(data.get(i));
        this.writer.write("\n");
    }

    /**
     * Write series data to csv file.
     *
     * @param header   Header of csv file. There should be defined names of columns.
     * @param dataList Data of csv file. Number values and some of them will have calculated averages.
     */
    public void writeSeries(List<String> header, List<List<Number>> dataList) throws IOException {
        this.writeData(header);

        int days = dataList.get(0).size();

        for (int i = 0; i < days; i++) {
            List<String> data = new ArrayList<>();
            data.add(Integer.toString(i));

            for (List<Number> list : dataList) {
                data.add(list.get(i).toString());
            }
            this.writeData(data);
        }


        for (List<Number> list : dataList) {
            float sum = 0;

            for (Number number : list) {
                sum += number.floatValue();
            }
            this.writer.write("," + sum / list.size());
        }

        this.writer.close();
    }
}
