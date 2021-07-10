package ipvc.estg.saveqr.ui.listapasta

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.observe
import androidx.navigation.Navigation
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ipvc.estg.saveqr.R
import ipvc.estg.saveqr.Swipes.SwipeToEditCallback
import ipvc.estg.saveqr.api.ServiceBuilder
import ipvc.estg.saveqr.api.api.endpoints.foldersEndpoint
import ipvc.estg.saveqr.api.api.models.Folders
import ipvc.estg.saveqr.api.api.models.FoldersReturn
import ipvc.estg.saveqr.popup_insertPasta
import kotlinx.android.synthetic.main.fragment_listapasta.view.*
import kotlinx.android.synthetic.main.popup_addpasta.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

class ListaPastaFragment : Fragment() {

    private lateinit var listaPastaViewModel: ListaPastaViewModel
    var idUser: Any? = null;
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(

        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
       listaPastaViewModel =
                ViewModelProviders.of(this).get(ListaPastaViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_listapasta, container, false)
        val textView4: TextView = root.findViewById(R.id.textView4)
        listaPastaViewModel.text.observe(viewLifecycleOwner, Observer {
       //     textView.text = it

        })


        val recyclerView = root.findViewById<RecyclerView>(R.id.recyclerView)


        val adapter = PastaAdapter()
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        val request = ServiceBuilder.buildService(foldersEndpoint::class.java)
        val call = request.getFolders()
        val allPastasLiveData = MutableLiveData<List<Folders?>>()

        call.enqueue(object  : Callback<FoldersReturn> {
            override fun onResponse(call: Call<FoldersReturn>, response: Response<FoldersReturn>) {

                if(response.isSuccessful) {
                    var arrAllReports: Array<Folders?> = arrayOfNulls<Folders>(response.body()!!.data.size)

                    for ((index, item) in response.body()!!.data.withIndex()) {
                        arrAllReports[index] = item
                    }

                    var allReports: List<Folders?> = arrAllReports.asList()

                    allPastasLiveData.value = allReports


                    allPastasLiveData.observe(requireActivity()) { reports ->
                        reports.let { adapter.submitList(it) }
                    }

                }else{

                }
            }

            override fun onFailure(call: Call<FoldersReturn>, t: Throwable) {
                Toast.makeText( activity, "DEU ERRO", Toast.LENGTH_LONG).show()
            }
        })

        textView4.setOnClickListener(Navigation.createNavigateOnClickListener(R.id.nav_listaQrFragment, null))
        val loginShared: SharedPreferences? =
            activity?.getSharedPreferences(getString(R.string.login_p), Context.MODE_PRIVATE)
        val idUser = loginShared?.getInt(getString(R.string.idLogin), 0)
        root.add.setOnClickListener {

            val mDialogView = LayoutInflater.from(requireActivity()).inflate(R.layout.popup_addpasta, null)
            val mBuilder = AlertDialog.Builder(requireActivity()).setView(mDialogView)
            val mAlertDialog = mBuilder.show()

            mDialogView.addpasta.setOnClickListener {
                val formatter = DateTimeFormatter.ofPattern("uuuu-MM-dd HH:mm:ss")      //data
                val usersDesiredTimeZone: ZoneId = ZoneId.of("Europe/Lisbon")           //fuso Horario
                val now: ZonedDateTime = ZonedDateTime.now(usersDesiredTimeZone)
                val text: String = now.format(formatter)
                val nomepasta = mDialogView.email.text.toString()

                val pasta = mDialogView.email.text.toString()
                Toast.makeText(activity, "Sucesso! - "+pasta, Toast.LENGTH_SHORT).show();
                mAlertDialog.dismiss()
                val request = ServiceBuilder.buildService(foldersEndpoint::class.java)
                val call = request.postFolders(
                    nomepasta,
                    text,
                    idUser as Int?
                )
                call.enqueue(object : Callback<Folders> {
                    override fun onResponse(
                        call: Call<Folders>,
                        response: Response<Folders>
                    ) {


                        if (response.isSuccessful) {
                            Log.d("***", "Sucesso")

                            // communicator.passDataconn(root.nome.text.toString())
                            //  communicator.passDataconn(id,root.nome.text.toString(),root.username.text.toString(),root.email.text.toString(),root.password.text.toString())
                            // getActivity()?.getSupportFragmentManager()?.beginTransaction().remove(this@RegistarFragment).commit();


                        } else {
                            Log.d("***", "Falhou")

                        }

                    }

                    override fun onFailure(call: Call<Folders>, t: Throwable) {
                        TODO("Not yet implemented")
                    }

                }
                )}
            }

        adapter.setOnItemClick(object : PastaAdapter.onItemClick {
            override fun onViewClick(position: Int) {
                Log.d("Pastas", "CLICAR CLICAR")
                val id: Int = allPastasLiveData.value?.get(position)?.id ?: 0


            }

        })

        val swipeHandlerEdit = object : SwipeToEditCallback(requireContext()) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position: Int = viewHolder.adapterPosition
                val id: Int = allPastasLiveData.value?.get(position)?.id ?: 0
                val pastaTemp: Folders? = allPastasLiveData.value!![position]

                val intent = Intent(requireContext(), popup_insertPasta::class.java)
                intent.putExtra("id", pastaTemp!!.id)
                intent.putExtra("name", pastaTemp!!.nome)
                intent.putExtra("img", pastaTemp!!.img)
                intent.putExtra("cor", pastaTemp!!.cor)
                intent.putExtra("part", pastaTemp!!.partilhado)
                intent.putExtra("dateAdd", pastaTemp!!.dataAdicionado)
                intent.putExtra("dateUpd", pastaTemp!!.dataAtualizado)
                intent.putExtra("catId", pastaTemp!!.categoryId)
                intent.putExtra("userId", pastaTemp!!.userId)
                startActivity(intent)
            }
        }

        val itemTouchHelperEdit = ItemTouchHelper(swipeHandlerEdit)
        itemTouchHelperEdit.attachToRecyclerView(recyclerView)

        return root





    }

    }

