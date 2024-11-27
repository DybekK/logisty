<?php

namespace Core\Persistence;

/**
 * @template T
 */
interface EntityBehavior
{
    /**
     * @return T
     */
    public function toModel();
}
