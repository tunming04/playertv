package com.player.tv.data.local;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityDeletionOrUpdateAdapter;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlinx.coroutines.flow.Flow;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class RecentDao_Impl implements RecentDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<RecentEntity> __insertionAdapterOfRecentEntity;

  private final EntityDeletionOrUpdateAdapter<RecentEntity> __deletionAdapterOfRecentEntity;

  private final EntityDeletionOrUpdateAdapter<RecentEntity> __updateAdapterOfRecentEntity;

  private final SharedSQLiteStatement __preparedStmtOfDeleteRecentByChannelId;

  public RecentDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfRecentEntity = new EntityInsertionAdapter<RecentEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `recent` (`id`,`channelId`,`playlistId`,`channelName`,`channelLogo`,`watchedAt`,`playbackPosition`) VALUES (nullif(?, 0),?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final RecentEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getChannelId());
        statement.bindString(3, entity.getPlaylistId());
        statement.bindString(4, entity.getChannelName());
        if (entity.getChannelLogo() == null) {
          statement.bindNull(5);
        } else {
          statement.bindString(5, entity.getChannelLogo());
        }
        statement.bindLong(6, entity.getWatchedAt());
        statement.bindLong(7, entity.getPlaybackPosition());
      }
    };
    this.__deletionAdapterOfRecentEntity = new EntityDeletionOrUpdateAdapter<RecentEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `recent` WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final RecentEntity entity) {
        statement.bindLong(1, entity.getId());
      }
    };
    this.__updateAdapterOfRecentEntity = new EntityDeletionOrUpdateAdapter<RecentEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `recent` SET `id` = ?,`channelId` = ?,`playlistId` = ?,`channelName` = ?,`channelLogo` = ?,`watchedAt` = ?,`playbackPosition` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final RecentEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getChannelId());
        statement.bindString(3, entity.getPlaylistId());
        statement.bindString(4, entity.getChannelName());
        if (entity.getChannelLogo() == null) {
          statement.bindNull(5);
        } else {
          statement.bindString(5, entity.getChannelLogo());
        }
        statement.bindLong(6, entity.getWatchedAt());
        statement.bindLong(7, entity.getPlaybackPosition());
        statement.bindLong(8, entity.getId());
      }
    };
    this.__preparedStmtOfDeleteRecentByChannelId = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM recent WHERE channelId = ?";
        return _query;
      }
    };
  }

  @Override
  public Object insertRecent(final RecentEntity recent,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfRecentEntity.insert(recent);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteRecent(final RecentEntity recent,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __deletionAdapterOfRecentEntity.handle(recent);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object updateRecent(final RecentEntity recent,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfRecentEntity.handle(recent);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteRecentByChannelId(final String channelId,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteRecentByChannelId.acquire();
        int _argIndex = 1;
        _stmt.bindString(_argIndex, channelId);
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfDeleteRecentByChannelId.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<RecentEntity>> getRecentChannels(final int limit) {
    final String _sql = "SELECT * FROM recent ORDER BY watchedAt DESC LIMIT ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, limit);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"recent"}, new Callable<List<RecentEntity>>() {
      @Override
      @NonNull
      public List<RecentEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfChannelId = CursorUtil.getColumnIndexOrThrow(_cursor, "channelId");
          final int _cursorIndexOfPlaylistId = CursorUtil.getColumnIndexOrThrow(_cursor, "playlistId");
          final int _cursorIndexOfChannelName = CursorUtil.getColumnIndexOrThrow(_cursor, "channelName");
          final int _cursorIndexOfChannelLogo = CursorUtil.getColumnIndexOrThrow(_cursor, "channelLogo");
          final int _cursorIndexOfWatchedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "watchedAt");
          final int _cursorIndexOfPlaybackPosition = CursorUtil.getColumnIndexOrThrow(_cursor, "playbackPosition");
          final List<RecentEntity> _result = new ArrayList<RecentEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final RecentEntity _item;
            final int _tmpId;
            _tmpId = _cursor.getInt(_cursorIndexOfId);
            final String _tmpChannelId;
            _tmpChannelId = _cursor.getString(_cursorIndexOfChannelId);
            final String _tmpPlaylistId;
            _tmpPlaylistId = _cursor.getString(_cursorIndexOfPlaylistId);
            final String _tmpChannelName;
            _tmpChannelName = _cursor.getString(_cursorIndexOfChannelName);
            final String _tmpChannelLogo;
            if (_cursor.isNull(_cursorIndexOfChannelLogo)) {
              _tmpChannelLogo = null;
            } else {
              _tmpChannelLogo = _cursor.getString(_cursorIndexOfChannelLogo);
            }
            final long _tmpWatchedAt;
            _tmpWatchedAt = _cursor.getLong(_cursorIndexOfWatchedAt);
            final long _tmpPlaybackPosition;
            _tmpPlaybackPosition = _cursor.getLong(_cursorIndexOfPlaybackPosition);
            _item = new RecentEntity(_tmpId,_tmpChannelId,_tmpPlaylistId,_tmpChannelName,_tmpChannelLogo,_tmpWatchedAt,_tmpPlaybackPosition);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Object getRecentByChannel(final String channelId,
      final Continuation<? super RecentEntity> $completion) {
    final String _sql = "SELECT * FROM recent WHERE channelId = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, channelId);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<RecentEntity>() {
      @Override
      @Nullable
      public RecentEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfChannelId = CursorUtil.getColumnIndexOrThrow(_cursor, "channelId");
          final int _cursorIndexOfPlaylistId = CursorUtil.getColumnIndexOrThrow(_cursor, "playlistId");
          final int _cursorIndexOfChannelName = CursorUtil.getColumnIndexOrThrow(_cursor, "channelName");
          final int _cursorIndexOfChannelLogo = CursorUtil.getColumnIndexOrThrow(_cursor, "channelLogo");
          final int _cursorIndexOfWatchedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "watchedAt");
          final int _cursorIndexOfPlaybackPosition = CursorUtil.getColumnIndexOrThrow(_cursor, "playbackPosition");
          final RecentEntity _result;
          if (_cursor.moveToFirst()) {
            final int _tmpId;
            _tmpId = _cursor.getInt(_cursorIndexOfId);
            final String _tmpChannelId;
            _tmpChannelId = _cursor.getString(_cursorIndexOfChannelId);
            final String _tmpPlaylistId;
            _tmpPlaylistId = _cursor.getString(_cursorIndexOfPlaylistId);
            final String _tmpChannelName;
            _tmpChannelName = _cursor.getString(_cursorIndexOfChannelName);
            final String _tmpChannelLogo;
            if (_cursor.isNull(_cursorIndexOfChannelLogo)) {
              _tmpChannelLogo = null;
            } else {
              _tmpChannelLogo = _cursor.getString(_cursorIndexOfChannelLogo);
            }
            final long _tmpWatchedAt;
            _tmpWatchedAt = _cursor.getLong(_cursorIndexOfWatchedAt);
            final long _tmpPlaybackPosition;
            _tmpPlaybackPosition = _cursor.getLong(_cursorIndexOfPlaybackPosition);
            _result = new RecentEntity(_tmpId,_tmpChannelId,_tmpPlaylistId,_tmpChannelName,_tmpChannelLogo,_tmpWatchedAt,_tmpPlaybackPosition);
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
