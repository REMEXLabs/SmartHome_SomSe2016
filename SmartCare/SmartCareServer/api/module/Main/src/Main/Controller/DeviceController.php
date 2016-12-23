<?php

namespace Main\Controller;

use Application\Entity\Patient;
use Zend\Mvc\Controller\AbstractRestfulController;

/**
 *
 */
class DeviceController extends AbstractRestfulController
{
	/**
	 * Return list of resources
	 *
	 * @return array
	 */
	public function getList()
	{
        $deviceModel = $this->getServiceLocator()->get('DeviceModel');
        $devicess = $deviceModel->getDevices();

		return $devicess;
	}

	/**
	 * Return single resource
	 *
	 * @param mixed $id
	 * @return mixed
	 */
	public function get($id) {

        return array('not implemented');
    }

	/**
	 * Create a new resource
	 *
	 * @param mixed $data
	 * @return mixed
	 */
	public function create($data) {
        return array('not implemented');
    }

	/**
	 * Update an existing resource
	 *
	 * @param mixed $id
	 * @param mixed $data
	 * @return mixed
	 */
	public function update($id, $data) {
        return array('not implemented');
    }

	/**
	 * Delete an existing resource
	 *
	 * @param  mixed $id
	 * @return mixed
	 */
	public function delete($id) {}
}
