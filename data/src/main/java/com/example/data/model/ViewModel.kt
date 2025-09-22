//package com.example.data.model
//
//import androidx.lifecycle.ViewModel
//import androidx.lifecycle.viewModelScope
//import com.example.data.database.Juegos
//import com.example.data.repository.JuegoRepository
//import kotlinx.coroutines.flow.MutableStateFlow
//import kotlinx.coroutines.flow.StateFlow
//import kotlinx.coroutines.launch
//
//class JuegoViewModel(private val repository: JuegoRepository) : ViewModel() {
//
//    private val _juegos = MutableStateFlow<List<Juegos>>(emptyList())
//    val juegos: StateFlow<List<Juegos>> get() = _juegos
//
//    fun cargarJuegos() {
//        viewModelScope.launch {
//            _juegos.value = repository.obtenerJuegos()
//        }
//    }
//
//    fun agregarJuego(juego: Juegos) {
//        viewModelScope.launch {
//            repository.agregarJuego(juego)
//            cargarJuegos()
//        }
//    }
//
//    fun actualizarJuego(juego: Juegos) {
//        viewModelScope.launch {
//            repository.actualizarJuego(juego)
//            cargarJuegos()
//        }
//    }
//
//    fun eliminarJuego(juego: Juegos) {
//        viewModelScope.launch {
//            repository.eliminarJuego(juego)
//            cargarJuegos()
//        }
//    }
//}
