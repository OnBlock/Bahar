package com.baharmc.loader.utils.version;

import com.baharmc.loader.utils.FileSystemUtil;
import com.google.gson.stream.JsonReader;
import org.cactoos.Scalar;
import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.regex.Matcher;

public final class GetVersion implements Scalar<McVersion> {

    @NotNull
    private final Path gameJar;

    public GetVersion(@NotNull Path gameJar) {
        this.gameJar = gameJar;
    }

    @Override
    public McVersion value() {
        try (FileSystemUtil.FileSystemDelegate jarFs = FileSystemUtil.getJarFileSystem(gameJar, false)) {
            FileSystem fs = jarFs.get();
            Path file;
            final McVersion mcVersion;

            if (Files.isRegularFile(file = fs.getPath("version.json"))) {
                mcVersion = fromVersionJson(Files.newInputStream(file));

                if (mcVersion.getRaw().isEmpty() && mcVersion.getNormalized().isEmpty()) {
                    return mcVersion;
                }
            } else if (Files.isRegularFile(file = fs.getPath("net/minecraft/realms/RealmsSharedConstants.class"))) {
                mcVersion = fromAnalyzer(Files.newInputStream(file), new FieldStringConstantVisitor("VERSION_STRING"));
            } else if (Files.isRegularFile(file = fs.getPath("net/minecraft/realms/RealmsBridge.class"))) {
                mcVersion = fromAnalyzer(Files.newInputStream(file), new MethodConstantRetVisited("getVersionString"));
            } else if (Files.isRegularFile(file = fs.getPath("net/minecraft/server/MinecraftServer.class"))) {
                mcVersion = fromAnalyzer(Files.newInputStream(file), new MethodConstantVisitor("run"));
            } else if (Files.isRegularFile(file = fs.getPath("net/minecraft/client/Minecraft.class"))) {
              mcVersion = fromAnalyzer(Files.newInputStream(file), new MethodConstantRetVisited(""));
            } else {
                mcVersion = new McVersion("", "");
            }

            if (!mcVersion.getRaw().isEmpty() && !mcVersion.getNormalized().isEmpty()) {
                return mcVersion;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return fromFileName(gameJar.getFileName().toString());
    }

    @NotNull
    private McVersion fromVersionJson(InputStream is) {
        try (JsonReader reader = new JsonReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
            String name = null;
            String release = null;

            reader.beginObject();

            while (reader.hasNext()) {
                switch (reader.nextName()) {
                    case "name": name = reader.nextString(); break;
                    case "release_target": release = reader.nextString(); break;
                    default: reader.skipValue();
                }
            }

            reader.endObject();

            if (name != null && release != null) {
                return new McVersion(name, release);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return new McVersion("","");
    }

    private McVersion fromFileName(String name) {
        int pos = name.lastIndexOf('.');
        if (pos > 0) name = name.substring(0, pos);

        return new McVersion(name, getRelease(name));
    }

    @NotNull
    private String getRelease(String version) {
        if (McVersion.RELEASE_PATTERN.matcher(version).matches()) return version;

        assert isProbableVersion(version);

        int pos = version.indexOf("-pre");
        if (pos >= 0) return version.substring(0, pos);

        pos = version.indexOf(" Pre-Release ");
        if (pos >= 0) return version.substring(0, pos);

        Matcher matcher =  McVersion.SNAPSHOT_PATTERN.matcher(version);

        if (matcher.matches()) {
            int year = Integer.parseInt(matcher.group(1));
            int week = Integer.parseInt(matcher.group(2));

            if (year == 19 && week == 34) {
                return "1.15";
            } else if (year == 18 && week >= 43 || year == 19 && week <= 14) {
                return "1.14";
            } else if (year == 18 && week >= 30 && week <= 33) {
                return "1.13.1";
            } else if (year == 17 && week >= 43 || year == 18 && week <= 22) {
                return "1.13";
            } else if (year == 17 && week == 31) {
                return "1.12.1";
            } else if (year == 17 && week >= 6 && week <= 18) {
                return "1.12";
            } else if (year == 16 && week == 50) {
                return "1.11.1";
            } else if (year == 16 && week >= 32 && week <= 44) {
                return "1.11";
            } else if (year == 16 && week >= 20 && week <= 21) {
                return "1.10";
            } else if (year == 16 && week >= 14 && week <= 15) {
                return "1.9.3";
            } else if (year == 15 && week >= 31 || year == 16 && week <= 7) {
                return "1.9";
            } else if (year == 14 && week >= 2 && week <= 34) {
                return "1.8";
            } else if (year == 13 && week >= 47 && week <= 49) {
                return "1.7.4";
            } else if (year == 13 && week >= 36 && week <= 43) {
                return "1.7.2";
            } else if (year == 13 && week >= 16 && week <= 26) {
                return "1.6";
            } else if (year == 13 && week >= 11 && week <= 12) {
                return "1.5.1";
            } else if (year == 13 && week >= 1 && week <= 10) {
                return "1.5";
            } else if (year == 12 && week >= 49 && week <= 50) {
                return "1.4.6";
            } else if (year == 12 && week >= 32 && week <= 42) {
                return "1.4.2";
            } else if (year == 12 && week >= 15 && week <= 30) {
                return "1.3.1";
            } else if (year == 12 && week >= 3 && week <= 8) {
                return "1.2.1";
            } else if (year == 11 && week >= 47 || year == 12 && week <= 1) {
                return "1.1";
            }
        }

        return "";
    }

    private boolean isProbableVersion(String str) {
        return McVersion.VERSION_PATTERN.matcher(str).matches();
    }

    @NotNull
    private <T extends ClassVisitor & Analyzed> McVersion fromAnalyzer(InputStream is, T analyzer) {
        try {
            ClassReader cr = new ClassReader(is);
            cr.accept(analyzer, ClassReader.SKIP_FRAMES | ClassReader.SKIP_DEBUG);
            String result = analyzer.getResult();

            return new McVersion(result, getRelease(result));
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (Exception ignored) {
            }
        }

        return new McVersion("","");
    }

}
