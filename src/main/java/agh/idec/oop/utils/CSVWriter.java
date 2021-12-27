package agh.idec.oop.utils;

import javafx.scene.chart.XYChart;

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

    public void writeData(List<String> data) throws IOException {
        int i = 0;
        for (; i < data.size() - 1; i++) {
            this.writer.write(data.get(i) + ",");
        }
        this.writer.write(data.get(i));
        this.writer.write("\n");
    }

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

        this.writer.close();
    }
}
