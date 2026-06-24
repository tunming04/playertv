package com.player.tv.data.local;

import android.database.Cursor;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.CoroutinesRoom;
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
public final class EpgDao_Impl implements EpgDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<EpgProgramEntity> __insertionAdapterOfEpgProgramEntity;

  private final SharedSQLiteStatement __preparedStmtOfDeleteOldPrograms;

  private final SharedSQLiteStatement __preparedStmtOfDeleteAllPrograms;

  public EpgDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfEpgProgramEntity = new EntityInsertionAdapter<EpgProgramEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `epg_programs` (`id`,`channelId`,`title`,`description`,`startTime`,`endTime`,`icon`) VALUES (?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final EpgProgramEntity entity) {
        statement.bindString(1, entity.getId());
        statement.bindString(2, entity.getChannelId());
        statement.bindString(3, entity.getTitle());
        if (entity.getDescription() == null) {
          statement.bindNull(4);
        } else {
          statement.bindString(4, entity.getDescription());
        }
        statement.bindLong(5, entity.getStartTime());
        statement.bindLong(6, entity.getEndTime());
        if (entity.getIcon() == null) {
          statement.bindNull(7);
        } else {
          statement.bindString(7, entity.getIcon());
        }
      }
    };
    this.__preparedStmtOfDeleteOldPrograms = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM epg_programs WHERE endTime < ?";
        return _query;
      }
    };
    this.__preparedStmtOfDeleteAllPrograms = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM epg_programs";
        return _query;
      }
    };
  }

  @Override
  public Object insertPrograms(final List<EpgProgramEntity> programs,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfEpgProgramEntity.insert(programs);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteOldPrograms(final long time, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteOldPrograms.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, time);
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
          __preparedStmtOfDeleteOldPrograms.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteAllPrograms(final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteAllPrograms.acquire();
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
          __preparedStmtOfDeleteAllPrograms.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<EpgProgramEntity>> getProgramsForChannel(final String channelId,
      final long time) {
    final String _sql = "SELECT * FROM epg_programs WHERE channelId = ? AND startTime >= ? ORDER BY startTime ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindString(_argIndex, channelId);
    _argIndex = 2;
    _statement.bindLong(_argIndex, time);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"epg_programs"}, new Callable<List<EpgProgramEntity>>() {
      @Override
      @NonNull
      public List<EpgProgramEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfChannelId = CursorUtil.getColumnIndexOrThrow(_cursor, "channelId");
          final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
          final int _cursorIndexOfDescription = CursorUtil.getColumnIndexOrThrow(_cursor, "description");
          final int _cursorIndexOfStartTime = CursorUtil.getColumnIndexOrThrow(_cursor, "startTime");
          final int _cursorIndexOfEndTime = CursorUtil.getColumnIndexOrThrow(_cursor, "endTime");
          final int _cursorIndexOfIcon = CursorUtil.getColumnIndexOrThrow(_cursor, "icon");
          final List<EpgProgramEntity> _result = new ArrayList<EpgProgramEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final EpgProgramEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpChannelId;
            _tmpChannelId = _cursor.getString(_cursorIndexOfChannelId);
            final String _tmpTitle;
            _tmpTitle = _cursor.getString(_cursorIndexOfTitle);
            final String _tmpDescription;
            if (_cursor.isNull(_cursorIndexOfDescription)) {
              _tmpDescription = null;
            } else {
              _tmpDescription = _cursor.getString(_cursorIndexOfDescription);
            }
            final long _tmpStartTime;
            _tmpStartTime = _cursor.getLong(_cursorIndexOfStartTime);
            final long _tmpEndTime;
            _tmpEndTime = _cursor.getLong(_cursorIndexOfEndTime);
            final String _tmpIcon;
            if (_cursor.isNull(_cursorIndexOfIcon)) {
              _tmpIcon = null;
            } else {
              _tmpIcon = _cursor.getString(_cursorIndexOfIcon);
            }
            _item = new EpgProgramEntity(_tmpId,_tmpChannelId,_tmpTitle,_tmpDescription,_tmpStartTime,_tmpEndTime,_tmpIcon);
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
  public Flow<EpgProgramEntity> getCurrentProgram(final String channelId, final long time) {
    final String _sql = "SELECT * FROM epg_programs WHERE channelId = ? AND startTime <= ? AND endTime > ? LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 3);
    int _argIndex = 1;
    _statement.bindString(_argIndex, channelId);
    _argIndex = 2;
    _statement.bindLong(_argIndex, time);
    _argIndex = 3;
    _statement.bindLong(_argIndex, time);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"epg_programs"}, new Callable<EpgProgramEntity>() {
      @Override
      @Nullable
      public EpgProgramEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfChannelId = CursorUtil.getColumnIndexOrThrow(_cursor, "channelId");
          final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
          final int _cursorIndexOfDescription = CursorUtil.getColumnIndexOrThrow(_cursor, "description");
          final int _cursorIndexOfStartTime = CursorUtil.getColumnIndexOrThrow(_cursor, "startTime");
          final int _cursorIndexOfEndTime = CursorUtil.getColumnIndexOrThrow(_cursor, "endTime");
          final int _cursorIndexOfIcon = CursorUtil.getColumnIndexOrThrow(_cursor, "icon");
          final EpgProgramEntity _result;
          if (_cursor.moveToFirst()) {
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpChannelId;
            _tmpChannelId = _cursor.getString(_cursorIndexOfChannelId);
            final String _tmpTitle;
            _tmpTitle = _cursor.getString(_cursorIndexOfTitle);
            final String _tmpDescription;
            if (_cursor.isNull(_cursorIndexOfDescription)) {
              _tmpDescription = null;
            } else {
              _tmpDescription = _cursor.getString(_cursorIndexOfDescription);
            }
            final long _tmpStartTime;
            _tmpStartTime = _cursor.getLong(_cursorIndexOfStartTime);
            final long _tmpEndTime;
            _tmpEndTime = _cursor.getLong(_cursorIndexOfEndTime);
            final String _tmpIcon;
            if (_cursor.isNull(_cursorIndexOfIcon)) {
              _tmpIcon = null;
            } else {
              _tmpIcon = _cursor.getString(_cursorIndexOfIcon);
            }
            _result = new EpgProgramEntity(_tmpId,_tmpChannelId,_tmpTitle,_tmpDescription,_tmpStartTime,_tmpEndTime,_tmpIcon);
          } else {
            _result = null;
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

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
