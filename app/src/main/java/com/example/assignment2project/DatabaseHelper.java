package com.example.assignment2project;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;
import android.text.Html;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "quiz_app.db";
    private static final int DATABASE_VERSION = 1;

    // Table Tournaments
    private static final String TABLE_TOURNAMENTS = "tournaments";
    private static final String COL_T_ID = "id";
    private static final String COL_T_NAME = "name";
    private static final String COL_T_CATEGORY = "category";
    private static final String COL_T_DIFFICULTY = "difficulty";
    private static final String COL_T_START_DATE = "start_date";
    private static final String COL_T_END_DATE = "end_date";
    private static final String COL_T_RATING = "rating";

    // Table Questions
    private static final String TABLE_QUESTIONS = "questions";
    private static final String COL_Q_ID = "id";
    private static final String COL_Q_TOURNAMENT_ID = "tournament_id";
    private static final String COL_Q_QUESTION = "question";
    private static final String COL_Q_CATEGORY = "category";
    private static final String COL_Q_TYPE = "type";
    private static final String COL_Q_DIFFICULTY = "difficulty";
    private static final String COL_Q_CORRECT_ANSWER = "correct_answer";
    private static final String COL_Q_INCORRECT_ANSWERS = "incorrect_answers";

    private final Gson gson = new Gson();

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTournamentsTable = "CREATE TABLE " + TABLE_TOURNAMENTS + " (" +
                COL_T_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_T_NAME + " TEXT, " +
                COL_T_CATEGORY + " TEXT, " +
                COL_T_DIFFICULTY + " TEXT, " +
                COL_T_START_DATE + " TEXT, " +
                COL_T_END_DATE + " TEXT, " +
                COL_T_RATING + " REAL)";

        String createQuestionsTable = "CREATE TABLE " + TABLE_QUESTIONS + " (" +
                COL_Q_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_Q_TOURNAMENT_ID + " INTEGER, " +
                COL_Q_QUESTION + " TEXT, " +
                COL_Q_CATEGORY + " TEXT, " +
                COL_Q_TYPE + " TEXT, " +
                COL_Q_DIFFICULTY + " TEXT, " +
                COL_Q_CORRECT_ANSWER + " TEXT, " +
                COL_Q_INCORRECT_ANSWERS + " TEXT)";

        db.execSQL(createTournamentsTable);
        db.execSQL(createQuestionsTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TOURNAMENTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_QUESTIONS);
        onCreate(db);
    }

    public long insertTournament(Tournament tournament, List<QuestionItem> questionItems) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        long tournamentId = -1;
        try {
            ContentValues tValues = new ContentValues();
            tValues.put(COL_T_NAME, tournament.getName());
            tValues.put(COL_T_CATEGORY, tournament.getCategory());
            tValues.put(COL_T_DIFFICULTY, tournament.getDifficulty());
            tValues.put(COL_T_START_DATE, tournament.getStartDate());
            tValues.put(COL_T_END_DATE, tournament.getEndDate());
            tValues.put(COL_T_RATING, tournament.getRating());

            tournamentId = db.insert(TABLE_TOURNAMENTS, null, tValues);

            if (tournamentId != -1 && questionItems != null) {
                for (QuestionItem item : questionItems) {
                    ContentValues qValues = new ContentValues();
                    qValues.put(COL_Q_TOURNAMENT_ID, tournamentId);
                    qValues.put(COL_Q_QUESTION, decodeHtml(item.getQuestion()));
                    qValues.put(COL_Q_CATEGORY, decodeHtml(item.getCategory()));
                    qValues.put(COL_Q_TYPE, item.getType());
                    qValues.put(COL_Q_DIFFICULTY, item.getDifficulty());
                    qValues.put(COL_Q_CORRECT_ANSWER, decodeHtml(item.getCorrectAnswer()));

                    List<String> decodedIncorrect = new ArrayList<>();
                    if (item.getIncorrectAnswers() != null) {
                        for (String ans : item.getIncorrectAnswers()) {
                            decodedIncorrect.add(decodeHtml(ans));
                        }
                    }
                    qValues.put(COL_Q_INCORRECT_ANSWERS, gson.toJson(decodedIncorrect));

                    db.insert(TABLE_QUESTIONS, null, qValues);
                }
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
        return tournamentId;
    }

    public List<Tournament> getAllTournaments() {
        List<Tournament> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_TOURNAMENTS + " ORDER BY " + COL_T_ID + " DESC", null);
        if (cursor.moveToFirst()) {
            do {
                Tournament t = new Tournament();
                t.setId(cursor.getLong(cursor.getColumnIndexOrThrow(COL_T_ID)));
                t.setName(cursor.getString(cursor.getColumnIndexOrThrow(COL_T_NAME)));
                t.setCategory(cursor.getString(cursor.getColumnIndexOrThrow(COL_T_CATEGORY)));
                t.setDifficulty(cursor.getString(cursor.getColumnIndexOrThrow(COL_T_DIFFICULTY)));
                t.setStartDate(cursor.getString(cursor.getColumnIndexOrThrow(COL_T_START_DATE)));
                t.setEndDate(cursor.getString(cursor.getColumnIndexOrThrow(COL_T_END_DATE)));
                t.setRating(cursor.getFloat(cursor.getColumnIndexOrThrow(COL_T_RATING)));
                list.add(t);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }

    public Tournament getTournamentById(long id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_TOURNAMENTS + " WHERE " + COL_T_ID + " = ?", new String[]{String.valueOf(id)});
        Tournament t = null;
        if (cursor.moveToFirst()) {
            t = new Tournament();
            t.setId(cursor.getLong(cursor.getColumnIndexOrThrow(COL_T_ID)));
            t.setName(cursor.getString(cursor.getColumnIndexOrThrow(COL_T_NAME)));
            t.setCategory(cursor.getString(cursor.getColumnIndexOrThrow(COL_T_CATEGORY)));
            t.setDifficulty(cursor.getString(cursor.getColumnIndexOrThrow(COL_T_DIFFICULTY)));
            t.setStartDate(cursor.getString(cursor.getColumnIndexOrThrow(COL_T_START_DATE)));
            t.setEndDate(cursor.getString(cursor.getColumnIndexOrThrow(COL_T_END_DATE)));
            t.setRating(cursor.getFloat(cursor.getColumnIndexOrThrow(COL_T_RATING)));
        }
        cursor.close();
        return t;
    }

    public List<Question> getQuestionsForTournament(long tournamentId) {
        List<Question> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_QUESTIONS + " WHERE " + COL_Q_TOURNAMENT_ID + " = ?", new String[]{String.valueOf(tournamentId)});
        Type listType = new TypeToken<List<String>>() {}.getType();

        if (cursor.moveToFirst()) {
            do {
                Question q = new Question();
                q.setId(cursor.getLong(cursor.getColumnIndexOrThrow(COL_Q_ID)));
                q.setTournamentId(cursor.getLong(cursor.getColumnIndexOrThrow(COL_Q_TOURNAMENT_ID)));
                q.setQuestion(cursor.getString(cursor.getColumnIndexOrThrow(COL_Q_QUESTION)));
                q.setCategory(cursor.getString(cursor.getColumnIndexOrThrow(COL_Q_CATEGORY)));
                q.setType(cursor.getString(cursor.getColumnIndexOrThrow(COL_Q_TYPE)));
                q.setDifficulty(cursor.getString(cursor.getColumnIndexOrThrow(COL_Q_DIFFICULTY)));
                q.setCorrectAnswer(cursor.getString(cursor.getColumnIndexOrThrow(COL_Q_CORRECT_ANSWER)));

                String jsonIncorrect = cursor.getString(cursor.getColumnIndexOrThrow(COL_Q_INCORRECT_ANSWERS));
                List<String> incorrectList = gson.fromJson(jsonIncorrect, listType);
                if (incorrectList == null) incorrectList = new ArrayList<>();
                q.setIncorrectAnswers(incorrectList);

                list.add(q);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }

    public boolean updateTournament(long id, String name, String startDate, String endDate) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_T_NAME, name);
        values.put(COL_T_START_DATE, startDate);
        values.put(COL_T_END_DATE, endDate);

        int rows = db.update(TABLE_TOURNAMENTS, values, COL_T_ID + " = ?", new String[]{String.valueOf(id)});
        return rows > 0;
    }

    public boolean deleteTournament(long id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_QUESTIONS, COL_Q_TOURNAMENT_ID + " = ?", new String[]{String.valueOf(id)});
        int rows = db.delete(TABLE_TOURNAMENTS, COL_T_ID + " = ?", new String[]{String.valueOf(id)});
        return rows > 0;
    }

    public static String decodeHtml(String text) {
        if (text == null) return "";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return Html.fromHtml(text, Html.FROM_HTML_MODE_LEGACY).toString();
        } else {
            return Html.fromHtml(text).toString();
        }
    }
}
