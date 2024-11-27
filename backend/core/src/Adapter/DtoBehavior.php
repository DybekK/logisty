<?php

namespace Core\Adapter;

/**
 * @template T
 */
interface DtoBehavior
{
    /**
     * @return T
     */
    public function toModel();
}
