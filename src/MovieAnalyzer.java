import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;


public class MovieAnalyzer {
    private ArrayList<String> Message;

    public MovieAnalyzer(String dataset_path) throws IOException {
        int cnt = 1;
        this.Message = new ArrayList<>();
        FileReader fileReader = new FileReader(dataset_path,StandardCharsets.UTF_8);
        BufferedReader bufferedReader = new BufferedReader(fileReader);
        String line = bufferedReader.readLine();
        while (line != null) {
            if (cnt == 1) {
                cnt++;
                line = bufferedReader.readLine();
                continue;
            }
            this.Message.add(line);
            line = bufferedReader.readLine();
        }
    }

    public Map<Integer, Integer> getMovieCountByYear() {
        Map<Integer, Integer> ans = new TreeMap<>(new Comparator<Integer>() {
            @Override
            public int compare(Integer o1, Integer o2) {
                return -o1.compareTo(o2);
            }
        }
        );
        for (int i = 0; i < this.Message.size(); i++) {
            String line = this.Message.get(i);
            String[] values = line.trim().split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)");
            int year = Integer.parseInt(values[2]);
            if (!ans.containsKey(year)) {
                ans.put(year, 1);
            } else {
                int num = ans.get(year);
                num += 1;
                ans.put(year, num);
            }
        }
        return ans;
    }

    public Map<String, Integer> getMovieCountByGenre() {
        Map<String, Integer> temp = new HashMap<>();
        for (int i = 0; i < this.Message.size(); i++) {
            String line = this.Message.get(i);
            String[] values = line.trim().split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)");
            String[] Genres = values[5].trim().split(",");
            if (Genres.length != 1) {
                Genres[0] = Genres[0].substring(1);
                Genres[Genres.length - 1] = Genres[Genres.length - 1].substring(0, Genres[Genres.length - 1].length() - 1);
            }
            for (int j = 0; j < Genres.length; j++) {
                Genres[j] = Genres[j].trim();
                if (!temp.containsKey(Genres[j])) {
                    temp.put(Genres[j], 1);
                } else {
                    int num = temp.get(Genres[j]);
                    num += 1;
                    temp.put(Genres[j], num);
                }
            }
        }
        List<Map.Entry<String, Integer>> list = new ArrayList<Map.Entry<String, Integer>>(temp.entrySet());
        Collections.sort(list, new Comparator<Map.Entry<String, Integer>>() {
            @Override
            public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
                if (!Objects.equals(o1.getValue(), o2.getValue()))
                    return -(o1.getValue() - o2.getValue());
                else
                    return -(o2.getKey().compareTo(o1.getKey()));
            }
        });
        Map<String, Integer> ans = new LinkedHashMap<>();
        for (int i = 0; i < list.size(); i++) {
            ans.put(list.get(i).getKey(), list.get(i).getValue());
        }
        return ans;
    }

    public Map<List<String>, Integer> getCoStarCount() {
        Map<List<String>, Integer> temp = new HashMap<>();
        for (int i = 0; i < this.Message.size(); i++) {
            String line = this.Message.get(i);
            String[] values = line.trim().split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)");
            String[] Stars = new String[4];
            Stars[0] = values[10];
            Stars[1] = values[11];
            Stars[2] = values[12];
            Stars[3] = values[13];
            Arrays.sort(Stars);
            for (int j = 0; j < 4; j++) {
                for (int k = j + 1; k < 4; k++) {
                    List<String> pairs = new ArrayList<>();
                    pairs.add(Stars[j]);
                    pairs.add(Stars[k]);

                    if (!temp.containsKey(pairs)) {
                        temp.put(pairs, 1);
                    } else {
                        int num = temp.get(pairs);
                        num += 1;
                        temp.replace(pairs, num);
                    }
                }
            }
        }

        return temp;
    }

    public List<String> getTopMovies(int top_k, String by) {
        List<String> ans = new ArrayList<>();
        if (Objects.equals(by, "runtime")) {
            Map<String, Integer> map = new HashMap<>();
            for (int i = 0; i < this.Message.size(); i++) {
                String line = this.Message.get(i);
                String[] values = line.trim().split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)");
                String time = values[4].substring(0, values[4].length() - 4);
                int t = Integer.parseInt(time);
                char [] name = values[1].toCharArray();
                if(name[0] == '"'){
                    values[1] = values[1].substring(1,values[1].length() - 1 );
                }
                if(map.containsKey(values[1])){
                    values[1] = values[1] + " ";
                }
                map.put(values[1], t);

            }
            List<Map.Entry<String, Integer>> list = new ArrayList<Map.Entry<String, Integer>>(map.entrySet());
            Collections.sort(list, new Comparator<Map.Entry<String, Integer>>() {
                @Override
                public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
                    if (!Objects.equals(o1.getValue(), o2.getValue()))
                        return -(o1.getValue() - o2.getValue());
                    else
                    {
                        int length = Math.min(o1.getKey().toCharArray().length,o2.getKey().toCharArray().length);
                        for (int i = 0;i < length;i++) {
                            if(o1.getKey().toCharArray()[i] != o2.getKey().toCharArray()[i]){
                                return (o1.getKey().toCharArray()[i]) - (o2.getKey().toCharArray()[i]);
                            }
                        }
                        return (o1.getKey().length() - o2.getKey().length());
                    }
                }
            });
            for (int i = 0; i < top_k; i++) {
                char[] s = list.get(i).getKey().toCharArray();
                String key = list.get(i).getKey();
                if (s[s.length - 1] == ' '){
                    key = key.substring(0, key.length() - 1);
                }
//                if (s[0] == '"') {
//                    key = key.substring(1, key.length() - 1);
//                }
//                System.out.println(key);
                ans.add(key);
            }
        }

        else if (Objects.equals(by, "overview")) {
            Map<String, String> map = new HashMap<>();
            for (int i = 0; i < this.Message.size(); i++) {
                String line = this.Message.get(i);
                String[] values = line.trim().split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)");
                char[] ch = values[7].toCharArray();
                if (ch[0] == '"') {
                    if (ch[ch.length - 1] == '"') {
                        values[7] = values[7].substring(1, values[7].length() - 1);
                    }
                }
                char [] name = values[1].toCharArray();
                if(name[0] == '"'){
                    values[1] = values[1].substring(1,values[1].length() - 1 );
                }
                map.put(values[1], values[7]);
            }
            List<Map.Entry<String, String>> list = new ArrayList<Map.Entry<String, String>>(map.entrySet());
            Collections.sort(list, new Comparator<Map.Entry<String, String>>() {
                @Override
                public int compare(Map.Entry<String, String> o1, Map.Entry<String, String> o2) {
                    if (o1.getValue().length() != o2.getValue().length()) {
                        return -(o1.getValue().length() - o2.getValue().length());
                    } else {
                        int length = Math.min(o1.getKey().toCharArray().length,o2.getKey().toCharArray().length);
                        for (int i = 0;i < length;i++) {
                            if(o1.getKey().toCharArray()[i] != o2.getKey().toCharArray()[i]){
                                return (o1.getKey().toCharArray()[i]) - (o2.getKey().toCharArray()[i]);
                            }
                        }
                        return (o1.getKey().length() - o2.getKey().length());
                    }
                }
            });
            for (int i = 0; i < top_k; i++) {
                char[] s = list.get(i).getKey().toCharArray();
                String key = list.get(i).getKey();
                if (s[0] == '"') {
                    key = key.substring(1, key.length() - 1);
                }

                ans.add(key);
            }
        }
        return ans;
    }

    public List<String> getTopStars(int top_k, String by) {
        List<String> ans = new ArrayList<>();
        Map<String, Integer> nums = new HashMap<>();
        if (Objects.equals(by, "rating")) {
            Map<String, Double> total = new HashMap<>();
            for (int i = 0; i < this.Message.size(); i++) {
                String line = this.Message.get(i);
                String[] values = line.trim().split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)");
                String rating = values[6];
                String[] Stars = new String[4];
                Stars[0] = values[10];
                Stars[1] = values[11];
                Stars[2] = values[12];
                Stars[3] = values[13];
                System.out.println(Arrays.toString(Stars));
                float frating = Float.parseFloat(rating);
                for (int j = 0; j < 4; j++) {
                    if (!total.containsKey(Stars[j])) {
                        total.put(Stars[j], (double) frating);
                        nums.put(Stars[j], 1);
                    } else {
                        int num = nums.get(Stars[j]);
                        num += 1;
                        double totalRating = total.get(Stars[j]);
                        totalRating += (double) frating;
                        nums.put(Stars[j], num);
                        total.put(Stars[j], totalRating);
                    }
                }
            }
            Map<String, Double> rank = new HashMap<>();
            for (String key : nums.keySet()) {
                double result = total.get(key) / nums.get(key);
//                System.out.println(result);
                rank.put(key, result);
            }
            List<Map.Entry<String, Double>> list = new ArrayList<Map.Entry<String, Double>>(rank.entrySet());
            Collections.sort(list, new Comparator<Map.Entry<String, Double>>() {
                @Override
                public int compare(Map.Entry<String, Double> o1, Map.Entry<String, Double> o2) {
                    if (!Objects.equals(o1.getValue(), o2.getValue())) {
                        return -o1.getValue().compareTo(o2.getValue());
                    } else {
                        int length = Math.min(o1.getKey().toCharArray().length,o2.getKey().toCharArray().length);
                        for (int i = 0;i < length;i++) {
                            if(o1.getKey().toCharArray()[i] != o2.getKey().toCharArray()[i]){
                                return (o1.getKey().toCharArray()[i]) - (o2.getKey().toCharArray()[i]);
                            }
                        }
                        return (o1.getKey().length() - o2.getKey().length());
                    }
                }

            });
            for (int j = 0; j < top_k; j++) {
                ans.add(list.get(j).getKey());
            }
            System.out.println(ans);
        }

        else if (Objects.equals(by, "gross")) {
            Map<String, Long> total = new HashMap<>();
            for (int i = 0; i < this.Message.size(); i++) {
                String line = this.Message.get(i);
                String[] values = line.trim().split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)");
                if (values.length < 16) continue;
                String g = values[15];
                System.out.println(g);
                g = g.substring(1, g.length() - 1);
                String[] v = g.split(",");
                String v1 = " ";
                for (int j = 0; j < v.length; j++) {
                    v1 = v1 + v[j];
                }
                v1 = v1.trim();
                long gross = Long.parseLong(v1);
                String[] Stars = new String[4];
                Stars[0] = values[10];
                Stars[1] = values[11];
                Stars[2] = values[12];
                Stars[3] = values[13];
                for (int j = 0; j < 4; j++) {
                    if (!total.containsKey(Stars[j])) {
                        total.put(Stars[j], gross);
                        nums.put(Stars[j], 1);
                    } else {
                        int num = nums.get(Stars[j]);
                        num += 1;
                        long totalGross = total.get(Stars[j]);
                        totalGross += gross;
                        nums.put(Stars[j], num);
                        total.put(Stars[j], totalGross);
                    }
                }
            }
            Map<String, Double> rank = new HashMap<>();
            for (String key : nums.keySet()) {
                double result = total.get(key) / nums.get(key);
                System.out.println(result);
                rank.put(key, result);
            }
            List<Map.Entry<String, Double>> list = new ArrayList<Map.Entry<String, Double>>(rank.entrySet());
            Collections.sort(list, new Comparator<Map.Entry<String, Double>>() {
                @Override
                public int compare(Map.Entry<String, Double> o1, Map.Entry<String, Double> o2) {
                    if (!Objects.equals(o1.getValue(), o2.getValue())) {
                        return -o1.getValue().compareTo(o2.getValue());
                    } else{
                        int length = Math.min(o1.getKey().toCharArray().length,o2.getKey().toCharArray().length);
                        for (int i = 0;i < length;i++) {
                            if(o1.getKey().toCharArray()[i] != o2.getKey().toCharArray()[i]){
                                return (o1.getKey().toCharArray()[i]) - (o2.getKey().toCharArray()[i]);
                            }
                        }
                        return (o1.getKey().length() - o2.getKey().length());
                    }
                }

            });
            for (int j = 0; j < top_k; j++) {
                ans.add(list.get(j).getKey());
            }
        }
        return ans;
    }

    public List<String> searchMovies(String genre, float min_rating, int max_runtime) {
        ArrayList<String> ans = new ArrayList<>();

        for (int i = 0; i < this.Message.size(); i++) {
            boolean judgeGenre = false;
            boolean judgeRating = false;
            boolean judgeRuntime = false;
            String line = this.Message.get(i);
            String[] values = line.trim().split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)");
            String[] Genres = values[5].split(",");
            char[] c = Genres[0].toCharArray();
            if (c[0] == '"'){
                Genres[0] = Genres[0].substring(1);
                Genres[Genres.length-1] = Genres[Genres.length-1].substring(0,Genres[Genres.length-1].length() - 1);
            }
            for (int j = 0;j < Genres.length;j++){
                Genres[j] = Genres[j].trim();
            }

            for (int j = 0; j < Genres.length; j++) {
                Genres[j] = Genres[j].trim();
                if (Genres[j].equals(genre)) {
                    judgeGenre = true;
                    break;
                }
            }
            int runtime = Integer.parseInt(values[4].substring(0, values[4].length() - 4));

            if (runtime <= max_runtime) {
                judgeRuntime = true;
            }
            float rating = Float.parseFloat(values[6]);
            if (rating >= min_rating) {
                judgeRating = true;
            }
            if (judgeGenre && judgeRating && judgeRuntime) {
                if(values[1].toCharArray()[0] == '"'){
                    values[1] = values[1].substring(1,values[1].length() - 1);
                }
                ans.add(values[1]);
            }
        }
        Collections.sort(ans, new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                int length = Math.min(o1.toCharArray().length, o2.toCharArray().length);
                for (int i = 0; i < length; i++) {
                    if (o1.toCharArray()[i] != o2.toCharArray()[i]) {
                        return (o1.toCharArray()[i]) - (o2.toCharArray()[i]);
                    }
                }
                 return (o1.length() - o2.length());
            }
        });
        return ans;
    }
//}
//    public static void main(String[] args) throws IOException {
//        MovieAnalyzer m = new MovieAnalyzer("resources/imdb_top_500.csv");
////        m.getCoStarCount();
//        m.getTopMovies(200,"runtime");
//    }
}
