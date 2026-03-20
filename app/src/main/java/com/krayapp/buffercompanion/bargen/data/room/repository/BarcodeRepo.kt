package com.krayapp.buffercompanion.bargen.data.room.repository

import androidx.paging.PagingSource
import com.krayapp.buffercompanion.bargen.data.room.entity.BarcodeEntity
import com.krayapp.buffercompanion.bargen.domain.type.SortType

/**
 * Репозиторий для управления данными штрих-кодов.
 */
interface BarcodeRepo {
    /**
     * Возвращает общее количество записей штрих-кодов.
     */
    suspend fun recordsCount(): Int

    suspend fun pinnedCount(): Int

    /**
     * Добавляет новый или обновляет существующий штрих-код.
     *
     * @param barcodeEntity Объект штрих-кода.
     */
    suspend fun upsertBarcode(barcodeEntity: BarcodeEntity)
    suspend fun upsertBarcode(list: List<BarcodeEntity>)

    /**
     * Удаляет штрих-коды по списку их идентификаторов.
     *
     * @param ids Список ID для удаления.
     */
    suspend fun removeBarcodesByIds(ids: List<String>)

    /**
     * Увеличивает счетчик использования штрих-кода.
     *
     * @param id Идентификатор штрих-кода.
     */
    suspend fun incrementUsageCount(id: String)

    /**
     * Удаляет привязку указанного тега у всех штрих-кодов.
     *
     * @param tagId Идентификатор тега.
     */
    suspend fun removeTagFromBarcodes(tagId: String)

    /**
     * Возвращает источник данных для пагинации всех штрих-кодов с заданной сортировкой.
     *
     * @param sort Тип сортировки.
     */
    fun getAllBarcodesPaging(sort: SortType) : PagingSource<Int, BarcodeEntity>

    /**
     * Возвращает список закрепленных баркодов
     */
    suspend fun getPinnedBarcodes(): List<BarcodeEntity>

    /**
     * Возвращает источник данных для пагинации штрих-кодов, отфильтрованных по названию.
     *
     * @param filter Строка поиска.
     */
    fun getFilteredBarcodesByNamePaging(filter: String) : PagingSource<Int, BarcodeEntity>
    
    /**
     * Закрепляет штрих-код на определенной позиции.
     *
     * @param id Идентификатор штрих-кода.
     * @param position Позиция закрепления.
     */
    suspend fun pinBarcode(id: String, position: Int)

    /**
     * Снимает закрепление со штрих-кода.
     *
     * @param id Идентификатор штрих-кода.
     */
    suspend fun unpinBarcode(id: String)
}
