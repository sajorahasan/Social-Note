package elamien.abdullah.socialnote.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import androidx.paging.PagedList
import androidx.paging.RxPagedListBuilder
import elamien.abdullah.socialnote.database.Note
import elamien.abdullah.socialnote.database.NoteDao
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import org.koin.core.KoinComponent
import org.koin.core.inject

/**
 * Created by AbdullahAtta on 7/23/2019.
 */
class NoteRepository : INoteRepository, KoinComponent {


    private val mNotesDao : NoteDao by inject()
    private val mDisposables = CompositeDisposable()


    override fun loadPagedNotes() : LiveData<PagedList<Note>> {
        val factory : DataSource.Factory<Int, Note> = mNotesDao.getNotes()
        val mNotesList = MutableLiveData<PagedList<Note>>()

        val notesList = RxPagedListBuilder(
            factory, PagedList.Config
                .Builder()
                .setPageSize(20)
                .setEnablePlaceholders(true)
                .build()
        ).buildFlowable(BackpressureStrategy.LATEST)

        mDisposables.add(
            notesList.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    mNotesList.value = it
                }
        )
        return mNotesList
    }


    override fun insertNote(note : Note) : LiveData<Long> {
        val id = MutableLiveData<Long>()
        mDisposables.add(
            mNotesDao.insertNote(note)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableSingleObserver<Long>() {
                    override fun onSuccess(t : Long) {
                        id.value = t
                    }

                    override fun onError(e : Throwable) {
                    }

                })
        )
        return id
    }

    override fun getNote(noteId : Long) : LiveData<Note> {
        val note = MutableLiveData<Note>()
        mDisposables.add(
            Flowable.fromPublisher(
                mNotesDao.getNote(noteId)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
            )
                .subscribe {
                    note.value = it
                })
        return note
    }

    override fun updateNote(note : Note) {
        mDisposables.add(Observable
            .fromCallable { mNotesDao.updateNote(note) }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe())
    }

    override fun dispose() {
        mDisposables.dispose()
    }
}