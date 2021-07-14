package mca.cobalt.localizer;

import net.minecraft.util.text.LanguageMap;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Localizer {
    private final ArrayList<VarParser> registeredVarParsers = new ArrayList<>();

    public String localize(String key, String... vars) {
        return localize(key, null, vars);
    }

    private final static Pattern pattern = Pattern.compile("^[0-9]+$");

    public String localize(String key, String keyFallback, String... vars) {
        ArrayList<String> list = new ArrayList<>();
        Collections.addAll(list, vars);
        return localize(key, keyFallback, list);
    }

    public String localize(String key, String keyFallback, ArrayList<String> vars) {
        //text
        String result = getLocalizedString(key);

        //fallback text
        if (result.equals(key) && keyFallback != null) {
            result = getLocalizedString(keyFallback);
        }

        return parseVars(result, vars);
    }

    private String getLocalizedString(String key) {
        //filter all translations, which starts with the key and optionally has numbers behind
        List<String> responses = LanguageMap.getInstance().getLanguageData().entrySet().stream().filter(entry -> {
            String k = entry.getKey();
            return k.startsWith(key) && (k.length() == key.length() || pattern.matcher(k.substring(key.length())).matches());
        }).map(Map.Entry::getValue).collect(Collectors.toList());

        if (responses.size() > 0) {
            return responses.get(new Random().nextInt(responses.size()));
        }

        return key;
    }

    public void registerVarParser(VarParser parser) {
        this.registeredVarParsers.add(parser);
    }

    private String parseVars(String str, ArrayList<String> vars) {
        int index = 1;
        for (VarParser processor : registeredVarParsers) {
            try {
                str = processor.parse(str);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        String varString = "%v" + index + "%";
        while (str.contains("%v") && index < 10) { // signature of a var being present
            try {
                str = str.replaceAll(varString, vars.get(index - 1));
            } catch (IndexOutOfBoundsException e) {
                str = str.replaceAll(varString, "");
                //Cobalt.getLog().warn("Failed to replace variable in localized string: " + str);
            } finally {
                index++;
                varString = "%v" + index + "%";
            }
        }

        return str;
    }
}
