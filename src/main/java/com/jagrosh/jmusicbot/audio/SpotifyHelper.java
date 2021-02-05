package com.jagrosh.jmusicbot.audio;

import com.wrapper.spotify.SpotifyApi;
import com.wrapper.spotify.exceptions.SpotifyWebApiException;
import com.wrapper.spotify.model_objects.credentials.ClientCredentials;
import com.wrapper.spotify.model_objects.specification.Playlist;
import com.wrapper.spotify.model_objects.specification.Track;
import com.wrapper.spotify.requests.authorization.client_credentials.ClientCredentialsRequest;
import com.wrapper.spotify.requests.data.playlists.GetPlaylistRequest;
import com.wrapper.spotify.requests.data.tracks.GetTrackRequest;
import org.apache.commons.codec.binary.StringUtils;
import org.apache.hc.core5.http.ParseException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SpotifyHelper {

    public static String getSpotifySongTitle(String spotifySongLink, String clientId, String clientSecret) throws ParseException, SpotifyWebApiException, IOException {
        SpotifyApi spotifyApi = new SpotifyApi.Builder().setClientId(clientId).setClientSecret(clientSecret).build();
        ClientCredentialsRequest clientCredentialsRequest = spotifyApi.clientCredentials().build();
        spotifyApi.setAccessToken(clientCredentialsRequest.execute().getAccessToken());

        GetTrackRequest getTrackRequest = spotifyApi.getTrack(getIdFromSpotifyLink(spotifySongLink)).build();
        Track track = getTrackRequest.execute();
        String trackSearchString = track.getName();
        trackSearchString += " " + track.getAlbum().getArtists()[0].getName();
        return trackSearchString;
    }

    public static List<String> getPlaylistTitles(String spotifyPlaylistLink, String clientId, String clientSecret) throws ParseException, SpotifyWebApiException, IOException {
        SpotifyApi spotifyApi = new SpotifyApi.Builder().setClientId(clientId).setClientSecret(clientSecret).build();
        ClientCredentialsRequest clientCredentialsRequest = spotifyApi.clientCredentials().build();
        spotifyApi.setAccessToken(clientCredentialsRequest.execute().getAccessToken());

        GetPlaylistRequest getPlaylistRequest = spotifyApi.getPlaylist(getIdFromSpotifyLink(spotifyPlaylistLink)).build();
        Playlist playlist = getPlaylistRequest.execute();
        List<String> playlistTitles = new ArrayList<>();
        Arrays.stream(playlist.getTracks().getItems()).forEach(t -> {
            String trackSearchString = t.getTrack().getName();
            try {
                Track track = ((Track) t.getTrack());
                trackSearchString += " " + track.getAlbum().getArtists()[0].getName();
            } catch (Exception e) {
                // do nothing
            }
            playlistTitles.add(trackSearchString);
        });
        return playlistTitles;
    }

    // Examples
    // https://open.spotify.com/track/5DRf4dsRrtLl8EyiEmILOK?si=a4YBNgVfQHCKMss_wBsq1A
    // https://open.spotify.com/playlist/3SklcGJZd20H5VfwjzuJij?si=_AIhZT5ZRvqxB7zfa5dINg
    private static String getIdFromSpotifyLink(String link) {
        String playlist = "playlist";
        String track = "track";
        if (link.contains(playlist)) {
            link = link.substring(link.indexOf(playlist) + playlist.length() + 1);
            if (!link.contains("?")) {
                return link;
            }
            return link.substring(0, link.indexOf("?"));
        } else if (link.contains(track)) {
            link = link.substring(link.indexOf(track) + track.length() + 1);
            if (!link.contains("?")) {
                return link;
            }
            return link.substring(0, link.indexOf("?"));
        }
        return null;
    }
}
