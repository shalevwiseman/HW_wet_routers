import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Matrix {
    ArrayList<ArrayList<Double>> matrix;
    int n;
    public Matrix(int n) {
        this.n = n;
        matrix = new ArrayList<>(n);
        for (int i = 0; i < n; i++) {
            ArrayList<Double> row = new ArrayList<>(n);
            for (int j = 0; j < n; j++) {
                row.add(-1.0);  // initialize each element to -1
            }
            matrix.add(row);
        }
    }

    public void updateMat(int row, String data) {
        data = data.substring(1, data.length()-1);
        List<String> pairs = Arrays.asList(data.split("[(), ]+"));
        data = data.replace("(","").replace(")", "").replace(" ", "");
        List<String> list = Arrays.asList(data.split(","));
        for (int i = 0; i < list.size(); i += 2) {
            String xstr = list.get(i);
            String ystr = list.get(i+1);
            if (!xstr.isEmpty() && !ystr.isEmpty()) {
                int x = Integer.parseInt(xstr);
                double y = Double.parseDouble(ystr);
                matrix.get(x-1).set(row-1, y);
                matrix.get(row-1).set(x-1, y);
            }
        }
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < matrix.size(); i++) {
            for (int j = 0; j < matrix.get(i).size(); j++) {
                sb.append(matrix.get(i).get(j));
                sb.append(" ");
            }
            sb.append("\n");
        }
        return sb.toString();
    }
}
