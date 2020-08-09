package com.test.exoplayer

import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.android.exoplayer2.ExoPlaybackException
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.ConcatenatingMediaSource
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util

class MainActivity : AppCompatActivity(){

    private lateinit var playerInfo: String
    private lateinit var dataSourceFactory: DefaultDataSourceFactory
    private lateinit var mediaSourceList: ConcatenatingMediaSource
    private lateinit var playerView: PlayerView
    private lateinit var player: SimpleExoPlayer

    companion object {
        private const val TAG = "PlayerLog"
    }

    private fun setupPlayerSettings() {
        player = SimpleExoPlayer.Builder(this).build()
        playerView.player = player
        player.playWhenReady = true
        player.addListener(playerListener())
    }


    private fun playMedia(mediaSource: MediaSource) {
        player.prepare(mediaSource)
        player.playWhenReady = true
    }

    private fun createMediaSourceFromAssets(fileName: String): MediaSource {
        return ProgressiveMediaSource
            .Factory(dataSourceFactory)
            .createMediaSource(Uri.parse("asset:///$fileName"))
    }

    private fun resetSequence() {
        try {
            val mediaSource1 = createMediaSourceFromAssets("video 1.mp4")
            val mediaSource2 = createMediaSourceFromAssets("video 2.mp4")
            val mediaSource3 = createMediaSourceFromAssets("video 3.mp4")
            mediaSourceList = ConcatenatingMediaSource()
            mediaSourceList.addMediaSource(mediaSource1)
            mediaSourceList.addMediaSource(mediaSource2)
            mediaSourceList.addMediaSource(mediaSource3)
            player.prepare(mediaSourceList)
        } catch (error: Exception) {
            Log.e(TAG, "create media source error: ${error.message}")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        playerView = findViewById(R.id.player_view)
        playerInfo = Util.getUserAgent(applicationContext, "ExoPlayerTest")
        dataSourceFactory = DefaultDataSourceFactory(applicationContext, playerInfo)
        playerView.setKeepContentOnPlayerReset(true)
        playerView.requestFocus()
        resetSequence()
    }

    override fun onResume() {
        setupPlayerSettings()
        playMedia(mediaSourceList)
        super.onResume()
    }

    override fun onPause() {
        player.release()
        super.onPause()
    }

    override fun onStop() {
        player.release()
        super.onStop()
    }

    private fun playerListener(): Player.EventListener {
        return object : Player.EventListener {
            override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
                if (playWhenReady && playbackState == Player.STATE_READY) {
                    Log.d(TAG, "Player.STATE_READY")
                } else if (playbackState == Player.STATE_ENDED) {
                    Log.d(TAG, "Player.STATE_END")
                    player.seekTo(0, 0)
                }
            }

            override fun onPlayerError(error: ExoPlaybackException) {
                Log.e(TAG, "exoplayer error", error)
            }
        }
    }
}
