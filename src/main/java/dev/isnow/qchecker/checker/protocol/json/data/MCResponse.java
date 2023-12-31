package dev.isnow.qchecker.checker.protocol.json.data;

import com.google.gson.annotations.SerializedName;
import dev.isnow.qchecker.checker.protocol.json.rawData.Players;
import dev.isnow.qchecker.checker.protocol.json.rawData.Version;
import lombok.Getter;

@Getter
public class MCResponse
{
    @SerializedName("players")
    Players players;
    @SerializedName("version")
    Version version;
    @SerializedName("favicon")
    String favicon;
}
